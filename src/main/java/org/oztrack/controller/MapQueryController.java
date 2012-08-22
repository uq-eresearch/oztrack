package org.oztrack.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.gml2.GMLConfiguration;
import org.geotools.wfs.WFS;
import org.geotools.wfs.v1_1.WFSConfiguration;
import org.geotools.xml.Encoder;
import org.oztrack.app.Constants;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.util.RServeInterface;
import org.oztrack.view.AnimalDetectionsFeatureBuilder;
import org.oztrack.view.AnimalStartEndFeatureBuilder;
import org.oztrack.view.AnimalTrajectoryFeatureBuilder;
import org.oztrack.view.KMLMapQueryView;
import org.oztrack.view.ProjectsFeatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

@Controller
public class MapQueryController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("searchQuery")
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields("includeDeleted");
    }
    
    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(
        @RequestParam(value="projectId", required=false) Long projectId,
        @RequestParam(value="queryType", required=false) String queryType,
        @RequestParam(value="dateFrom", required=false) String dateFrom,
        @RequestParam(value="dateTo", required=false) String dateTo,
        @RequestParam(value="srs", required=false) String srs,
        @RequestParam(value="percent", required=false) Double percent,
        @RequestParam(value="h", required=false) String h,
        @RequestParam(value="alpha", required=false) Double alpha,
        @RequestParam(value="gridSize", required=false) Double gridSize
    )
    throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        if (projectId != null) {
            Project project = projectDao.getProjectById(Long.valueOf(projectId));
            searchQuery.setProject(project);
        }
        if (queryType != null) {
            searchQuery.setMapQueryType(MapQueryType.valueOf(queryType));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (dateFrom != null) {
            searchQuery.setFromDate(sdf.parse(dateFrom));
        }
        if (dateTo != null) {
            searchQuery.setToDate(sdf.parse(dateTo));
        }
        if (srs != null && !srs.isEmpty()) {
            String regex = "[a-zA-Z_-]+:[0-9]+";
            if (!srs.matches(regex)) {
                throw new RuntimeException("Invalid SRS code: must match " + regex);
            }
            searchQuery.setSrs(srs);
        }
        if (percent != null && !percent.isNaN()) {
            searchQuery.setPercent(percent);
        }
        if (h != null && !h.isEmpty()) {
            boolean validDouble = false;
            try {
                Double.parseDouble(h);
                validDouble = true;
            }
            catch (NumberFormatException e) {
            }
            String regex = "href|LSCV";
            if (!h.matches(regex) && !validDouble) {
                throw new RuntimeException("Invalid h value: must be number or match " + regex);
            }
            searchQuery.setH(h);
        }
        if (alpha != null && !alpha.isNaN()) {
            searchQuery.setAlpha(alpha);
        }
        if (gridSize != null && !gridSize.isNaN()) {
            searchQuery.setGridSize(gridSize);
        }
        return searchQuery;
    }
    
    @RequestMapping(value="/mapQueryKML", method=RequestMethod.GET)
    @PreAuthorize("#searchQuery.project.global or hasPermission(#searchQuery.project, 'read')")
    public View handleKMLQuery(@ModelAttribute(value="searchQuery") SearchQuery searchQuery) throws Exception {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        RServeInterface rServeInterface = new RServeInterface(positionFixList, searchQuery);
        File kmlFile = rServeInterface.createKml();
        return new KMLMapQueryView(kmlFile, searchQuery);
    }
    
    @RequestMapping(value="/mapQueryWFS", method=RequestMethod.POST)
    @PreAuthorize(
        "(#searchQuery.mapQueryType == T(org.oztrack.data.model.types.MapQueryType).PROJECTS) or " +
        "#searchQuery.project.global or " +
        "hasPermission(#searchQuery.project, 'read')"
    )
    public void handleWFSQuery(
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        HttpServletResponse response
    ) throws Exception {
        SimpleFeatureCollection featureCollection = null;
        switch (searchQuery.getMapQueryType()) {
            case PROJECTS: {
                featureCollection = new ProjectsFeatureBuilder(projectDao.getAll()).buildFeatureCollection();
                break;
            }
            case POINTS: {
                List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                Boolean includeDeleted = (searchQuery.getIncludeDeleted() != null) && searchQuery.getIncludeDeleted().booleanValue();
                featureCollection = new AnimalDetectionsFeatureBuilder(positionFixList, includeDeleted).buildFeatureCollection();
                break;
            }
            case LINES: {
                List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                featureCollection = new AnimalTrajectoryFeatureBuilder(positionFixList).buildFeatureCollection();
                break;
            }
            case START_END: {
                List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                featureCollection = new AnimalStartEndFeatureBuilder(positionFixList).buildFeatureCollection();
                break;
            }
            default:
                throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
        }
        
        FeatureCollectionType featureCollectionType = WfsFactory.eINSTANCE.createFeatureCollectionType();
        @SuppressWarnings("unchecked")
        EList<SimpleFeatureCollection> featureCollections = featureCollectionType.getFeature();
        featureCollections.add(featureCollection);

        WFSConfiguration wfsConfiguration = new WFSConfiguration();
        @SuppressWarnings("unchecked")
        Set<QName> wfsConfigurationProperties = wfsConfiguration.getProperties();
        wfsConfigurationProperties.add(GMLConfiguration.NO_FEATURE_BOUNDS);
        Encoder encoder = new Encoder(wfsConfiguration);
        encoder.getNamespaces().declarePrefix(Constants.namespacePrefix, Constants.namespaceURI);
        encoder.setIndenting(true);

        response.setContentType("text/xml");
        response.setHeader("Content-Encoding", "gzip");
        GZIPOutputStream gzipOutputStream = null;
        try {
            gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
            encoder.encode(featureCollectionType, WFS.FeatureCollection, gzipOutputStream);
        }
        catch (IOException e) {
            logger.error("Error writing output stream", e);
        }
        finally {
            IOUtils.closeQuietly(gzipOutputStream);
        }
    }
}
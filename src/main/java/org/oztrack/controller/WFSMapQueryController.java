package org.oztrack.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
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
import org.oztrack.data.model.types.MapLayerType;
import org.oztrack.view.AnimalDetectionsFeatureBuilder;
import org.oztrack.view.AnimalStartEndFeatureBuilder;
import org.oztrack.view.AnimalTrajectoryFeatureBuilder;
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

import com.vividsolutions.jts.geom.Polygon;

@Controller
public class WFSMapQueryController {
    protected final Log logger = LogFactory.getLog(getClass());

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("searchQuery")
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(
        @RequestParam(value="projectId", required=true) Long projectId,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString,
        @RequestParam(value="animalIds", required=false) List<Long> animalIds,
        @RequestParam(value="includeDeleted", required=false) Boolean includeDeleted
    ) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject((projectId != null) ? projectDao.getProjectById(projectId) : null);
        try {
            searchQuery.setFromDate(StringUtils.isNotBlank(fromDateString) ? dateFormat.parse(fromDateString) : null);
        }
        catch (ParseException e) {
            logger.error("Invalid fromDate", e);
        }
        try {
            searchQuery.setToDate(StringUtils.isNotBlank(toDateString) ? dateFormat.parse(toDateString) : null);
        }
        catch (ParseException e) {
            logger.error("Invalid toDate", e);
        }
        searchQuery.setAnimalIds(animalIds);
        searchQuery.setIncludeDeleted(includeDeleted);
        return searchQuery;
    }

    @RequestMapping(value="/projectsWFS", method=RequestMethod.POST)
    @PreAuthorize("permitAll")
    public void handleProjectsWFS(HttpServletResponse response) {
        List<Project> projects = projectDao.getAll();
        HashMap<Project, Range<Date>> dateRangeMap = new HashMap<Project, Range<Date>>();
        HashMap<Project, Polygon> boundingBoxMap = new HashMap<Project, Polygon>();
        for (Project project : projects) {
            dateRangeMap.put(project, projectDao.getDetectionDateRange(project, false));
            boundingBoxMap.put(project, projectDao.getBoundingBox(project));
        }
        SimpleFeatureCollection featureCollection =
            new ProjectsFeatureBuilder(projects, dateRangeMap, boundingBoxMap).buildFeatureCollection();
        writeWFSResponse(response, featureCollection);
    }

    @RequestMapping(value="/mapQueryWFS", method=RequestMethod.POST)
    @PreAuthorize("#searchQuery.project.global or hasPermission(#searchQuery.project, 'read')")
    public void handleQueryWFS(
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        @RequestParam(value="queryType", required=true) MapLayerType mapLayerType,
        HttpServletResponse response
    ) {
        SimpleFeatureCollection featureCollection = null;
        switch (mapLayerType) {
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
            default: {
                throw new RuntimeException("Unsupported map layer type: " + mapLayerType);
            }
        }
        writeWFSResponse(response, featureCollection);
    }

    private void writeWFSResponse(HttpServletResponse response, SimpleFeatureCollection featureCollection) {
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
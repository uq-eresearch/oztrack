package org.oztrack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import net.opengis.wfs.FeatureCollectionType;
import net.opengis.wfs.WfsFactory;

import org.apache.commons.io.IOUtils;
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
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.error.RServeInterfaceException;
import org.oztrack.util.RServeInterface;
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (dateFrom != null) {
            searchQuery.setFromDate(sdf.parse(dateFrom));
        }
        if (dateTo != null) {
            searchQuery.setToDate(sdf.parse(dateTo));
        }
        if (percent != null && !percent.isNaN()) {
            searchQuery.setPercent(percent);
        }
        if (h != null && !h.isEmpty()) {
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
    public void handleKMLQuery(
        HttpServletResponse response,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery
    ) {
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        RServeInterface rServeInterface = new RServeInterface(positionFixList, searchQuery);
        File kmlFile = null;
        try {
            kmlFile = rServeInterface.createKml();
        }
        catch (RServeInterfaceException e) {
            logger.error("RServeInterface exception", e);
            writeKMLQueryErrorResponse(response, e.getMessage());
            response.setStatus(500);
            return;
        }
        String filename = searchQuery.getMapQueryType().name().toLowerCase(Locale.ENGLISH) + ".kml";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
        response.setContentType("application/vnd.google-earth.kml+xml");
        response.setCharacterEncoding("UTF-8");
        FileInputStream kmlInputStream = null;
        try {
            kmlInputStream = new FileInputStream(kmlFile);
            IOUtils.copy(kmlInputStream, response.getOutputStream());
            kmlFile.delete();
        }
        catch (IOException e) {
            writeKMLQueryErrorResponse(response, "Error writing KML response.");
            response.setStatus(500);
            return;
        }
        finally {
            IOUtils.closeQuietly(kmlInputStream);
        }
    }

    private static void writeKMLQueryErrorResponse(HttpServletResponse response, String error) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        out.append("<?xml version=\"1.0\"?>\n");
        out.append("<map-query-kml-response xmlns=\"http://oztrack.org/xmlns#\">\n");
        out.append("    <error>" + StringUtils.trim(error) + "</error>\n");
        out.append("</map-query-kml-response>\n");
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
package org.oztrack.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import org.oztrack.data.access.AnalysisDao;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Analysis;
import org.oztrack.data.model.AnalysisParameter;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.error.RServeInterfaceException;
import org.oztrack.util.RServeInterface;
import org.oztrack.view.AnimalDetectionsFeatureBuilder;
import org.oztrack.view.AnimalStartEndFeatureBuilder;
import org.oztrack.view.AnimalTrajectoryFeatureBuilder;
import org.oztrack.view.ProjectsFeatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vividsolutions.jts.geom.Polygon;

@Controller
public class MapQueryController {
    protected final Log logger = LogFactory.getLog(getClass());

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @Autowired
    private AnalysisDao analysisDao;

    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

    @InitBinder("searchQuery")
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "fromDate",
            "toDate",
            "animalIds",
            "includeDeleted"
        );
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(
        @RequestParam(value="projectId", required=false) Long projectId,
        @RequestParam(value="queryType", required=false) String queryType,
        @RequestParam(value="percent", required=false) Double percent,
        @RequestParam(value="h", required=false) String h,
        @RequestParam(value="alpha", required=false) Double alpha,
        @RequestParam(value="gridSize", required=false) Double gridSize,
        @RequestParam(value="extent", required=false) Double extent
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
        if (extent != null && !extent.isNaN()) {
            searchQuery.setExtent(extent);
        }
        return searchQuery;
    }

    @RequestMapping(value="/mapQueryKML", method=RequestMethod.GET)
    @PreAuthorize("#searchQuery.project.global or hasPermission(#searchQuery.project, 'read')")
    public void handleKMLQuery(
        Authentication authentication,
        HttpServletResponse response,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery
    ) {
        if (permissionEvaluator.hasPermission(authentication, searchQuery.getProject(), "write")) {
            User currentUser = userDao.getByUsername((String) authentication.getPrincipal());
            saveAnalysis(searchQuery, currentUser);
        }
        writeKMLResponse(response, searchQuery);
    }

    private void saveAnalysis(SearchQuery searchQuery, User currentUser) {
        Analysis analysis = new Analysis();
        java.util.Date createDate = new java.util.Date();
        analysis.setCreateDate(createDate);
        analysis.setCreateUser(currentUser);
        analysis.setUpdateDate(createDate);
        analysis.setUpdateUser(currentUser);
        analysis.setProject(searchQuery.getProject());
        analysis.setAnalysisType(searchQuery.getMapQueryType());
        analysis.setFromDate(searchQuery.getFromDate());
        analysis.setToDate(searchQuery.getToDate());
        Set<Animal> animals = new HashSet<Animal>();
        for (Long animalId : searchQuery.getAnimalIds()) {
            animals.add(animalDao.getAnimalById(animalId));
        }
        analysis.setAnimals(animals);
        Set<AnalysisParameter> parameters = new HashSet<AnalysisParameter>();
        if (searchQuery.getAlpha() != null) {
            AnalysisParameter parameter = new AnalysisParameter();
            parameter.setAnalysis(analysis);
            parameter.setName("alpha");
            parameter.setValue(String.valueOf(searchQuery.getAlpha()));
            parameters.add(parameter);
        }
        if (searchQuery.getExtent() != null) {
            AnalysisParameter parameter = new AnalysisParameter();
            parameter.setAnalysis(analysis);
            parameter.setName("extent");
            parameter.setValue(String.valueOf(searchQuery.getExtent()));
            parameters.add(parameter);
        }
        if (searchQuery.getGridSize() != null) {
            AnalysisParameter parameter = new AnalysisParameter();
            parameter.setAnalysis(analysis);
            parameter.setName("gridSize");
            parameter.setValue(String.valueOf(searchQuery.getGridSize()));
            parameters.add(parameter);
        }
        if (searchQuery.getH() != null) {
            AnalysisParameter parameter = new AnalysisParameter();
            parameter.setAnalysis(analysis);
            parameter.setName("h");
            parameter.setValue(String.valueOf(searchQuery.getH()));
            parameters.add(parameter);
        }
        if (searchQuery.getPercent() != null) {
            AnalysisParameter parameter = new AnalysisParameter();
            parameter.setAnalysis(analysis);
            parameter.setName("percent");
            parameter.setValue(String.valueOf(searchQuery.getPercent()));
            parameters.add(parameter);
        }
        analysis.setParameters(parameters);
        analysisDao.save(analysis);
    }

    private void writeKMLResponse(HttpServletResponse response, SearchQuery searchQuery) {
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
    public void handleQueryWFS(@ModelAttribute(value="searchQuery") SearchQuery searchQuery, HttpServletResponse response) {
        SimpleFeatureCollection featureCollection = null;
        switch (searchQuery.getMapQueryType()) {
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
                throw new RuntimeException("Unsupported map query type: " + searchQuery.getMapQueryType());
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
package org.oztrack.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.geotools.factory.Hints;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.AnalysisType;
import org.oztrack.data.model.types.ArgosClass;
import org.oztrack.error.RserveInterfaceException;
import org.oztrack.util.RserveConnectionPool;
import org.oztrack.util.RserveInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@Controller
public class ProjectCleanseController {
    private final DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @Autowired
    AnimalDao animalDao;

    @Autowired
    private RserveConnectionPool rserveConnectionPool;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @RequestMapping(value="/projects/{id}/cleanse", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'write')")
    public String getCleanseView(Model model, @ModelAttribute(value="project") Project project) {
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("projectBoundingBox", projectDao.getBoundingBox(project, true));
        model.addAttribute("animalBoundingBoxes", projectDao.getAnimalBoundingBoxes(project, true));
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, true));
        model.addAttribute("analysisTypeList", Arrays.asList(AnalysisType.KALMAN, AnalysisType.KALMAN_SST));
        model.addAttribute("kalmanAnalysisType", AnalysisType.KALMAN);
        model.addAttribute("kalmanSstAnalysisType", AnalysisType.KALMAN_SST);
        model.addAttribute("argosClasses", ArgosClass.values());
        return "project-cleanse.html";
    }

    @RequestMapping(value="/projects/{id}/cleanse", method=RequestMethod.POST, produces="application/xml")
    @PreAuthorize("hasPermission(#project, 'write')")
    public void processCleanse(
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="operation", required=true) String operation,
        @RequestParam(value="mode", required=true) String mode,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString,
        @RequestParam(value="animalIds", required=false) List<Long> animalIds,
        @RequestParam(value="maxSpeed", required=false) Double maxSpeed,
        @RequestParam(value="minArgosClass", required=false) String minArgosClassCode,
        @RequestParam(value="maxDop", required=false) Double maxDop,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException, RserveInterfaceException {
        Date fromDate = null;
        Date toDate = null;
        try {
            if (StringUtils.isNotBlank(fromDateString)) {
                fromDate = isoDateFormat.parse(fromDateString);
            }
            if (StringUtils.isNotBlank(toDateString)) {
                toDate = (toDateString == null) ? null : isoDateFormat.parse(toDateString);
            }
        }
        catch (java.text.ParseException e1) {
            writeErrorResponse(response, "Invalid date parameters");
            return;
        }
        String[] polygonParams = request.getParameterValues("polygon");
        if (mode.equals("multi-polygon") && ((polygonParams == null) || polygonParams.length == 0)) {
            writeErrorResponse(response, "No polygons selected");
            return;
        }
        if (mode.equals("speed-filter") && ((maxSpeed == null) || maxSpeed.isNaN() || maxSpeed.isInfinite())) {
            writeErrorResponse(response, "No max speed entered");
            return;
        }
        if (mode.equals("argos-class") && ((minArgosClassCode == null) || minArgosClassCode.isEmpty())) {
            writeErrorResponse(response, "No minimum location class entered");
            return;
        }
        ArgosClass minArgosClass = ArgosClass.fromCode(minArgosClassCode);
        if (StringUtils.isNotBlank(minArgosClassCode) && (minArgosClass == null)) {
            writeErrorResponse(response, "Invalid location class entered");
            return;
        }
        if (mode.equals("dop") && ((maxDop == null) || maxDop.isNaN() || maxDop.isInfinite())) {
            writeErrorResponse(response, "No maximum DOP entered");
            return;
        }

        MultiPolygon multiPolygon = null;
        if ((polygonParams != null) && (polygonParams.length > 0)) {
            Hints hints = new Hints();
            hints.put(Hints.CRS, DefaultGeographicCRS.WGS84);
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(hints);
            WKTReader reader = new WKTReader(geometryFactory);
            ArrayList<Polygon> polygons = new ArrayList<Polygon>();
            for (String polygonWkt : polygonParams) {
                try {
                    Polygon polygon = (Polygon) reader.read(polygonWkt);
                    polygons.add(polygon);
                }
                catch (ParseException e) {
                    throw new RuntimeException("Error reading polygon: " + polygonWkt, e);
                }
            }
            multiPolygon = geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[0]));
        }

        Set<PositionFix> speedFilterPositionFixes = null;
        if (maxSpeed != null) {
            speedFilterPositionFixes = new HashSet<PositionFix>();
            SearchQuery searchQuery = new SearchQuery();
            searchQuery.setProject(project);
            searchQuery.setFromDate(fromDate);
            searchQuery.setToDate(toDate);
            searchQuery.setAnimalIds(animalIds);
            List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
            RserveInterface rserveInterface = new RserveInterface(rserveConnectionPool);
            Map<Long, Set<Date>> animalDates = rserveInterface.runSpeedFilter(project, positionFixList, maxSpeed);
            for (PositionFix positionFix : positionFixList) {
                Set<Date> dates = animalDates.get(positionFix.getAnimal().getId());
                // Need to create new java.util.Date here because positionFix.detectionTime is a java.sql.Timestamp.
                // Date and Timestamp have same hashCode but their equals methods differ, breaking contains call.
                if ((dates != null) && dates.contains(new Date(positionFix.getDetectionTime().getTime()))) {
                    speedFilterPositionFixes.add(positionFix);
                }
            }
        }

        if (operation.equals("delete")) {
            int numDeleted = positionFixDao.setDeleted(
                project,
                fromDate,
                toDate,
                animalIds,
                multiPolygon,
                speedFilterPositionFixes,
                minArgosClass,
                maxDop,
                true
            );
            positionFixDao.renumberPositionFixes(project, animalIds);
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <num-deleted>" + numDeleted + "</num-deleted>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(200);
            return;
        }
        else if (operation.equals("undelete")) {
            int numUndeleted = positionFixDao.setDeleted(
                project,
                fromDate,
                toDate,
                animalIds,
                multiPolygon,
                speedFilterPositionFixes,
                minArgosClass,
                maxDop,
                false
            );
            positionFixDao.renumberPositionFixes(project, animalIds);
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <num-undeleted>" + numUndeleted + "</num-undeleted>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(200);
            return;
        }
        else {
            writeErrorResponse(response, "Unknown operation: " + operation);
            return;
        }
    }

    private void writeErrorResponse(HttpServletResponse response, String message) throws IOException {
        PrintWriter out = response.getWriter();
        out.append("<?xml version=\"1.0\"?>\n");
        out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
        out.append("    <error>" + message + "</error>\n");
        out.append("</cleanse-response>\n");
        response.setStatus(400);
    }
}

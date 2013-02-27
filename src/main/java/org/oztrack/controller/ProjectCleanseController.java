package org.oztrack.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.ObjectPool;
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
import org.oztrack.error.RServeInterfaceException;
import org.oztrack.util.RServeInterface;
import org.rosuda.REngine.Rserve.RConnection;
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
    protected final Log logger = LogFactory.getLog(getClass());

    private final DateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @Autowired
    AnimalDao animalDao;

    @Autowired
    private ObjectPool<RConnection> rServeConnectionPool;

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
        model.addAttribute("projectBoundingBox", projectDao.getBoundingBox(project));
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, true));
        return "project-cleanse";
    }

    @RequestMapping(value="/projects/{id}/cleanse", method=RequestMethod.POST, produces="application/xml")
    @PreAuthorize("hasPermission(#project, 'write')")
    public void processCleanse(
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="fromDate", required=false) String fromDateString,
        @RequestParam(value="toDate", required=false) String toDateString,
        @RequestParam(value="maxSpeed", required=false) Double maxSpeed,
        @RequestParam(value="operation", defaultValue="delete") String operation,
        @RequestParam(value="animal") List<Long> animalIds,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException, RServeInterfaceException {
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
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <error>Invalid date parameters</error>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(200);
            return;
        }
        Hints hints = new Hints();
        hints.put(Hints.CRS, DefaultGeographicCRS.WGS84);
        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(hints);
        WKTReader reader = new WKTReader(geometryFactory);
        String[] polygonsWkt = request.getParameterValues("polygon");
        if (polygonsWkt == null) {
            polygonsWkt = new String[0];
        }
        ArrayList<Polygon> polygons = new ArrayList<Polygon>();
        for (String polygonWkt : polygonsWkt) {
            try {
                Polygon polygon = (Polygon) reader.read(polygonWkt);
                polygons.add(polygon);
            }
            catch (ParseException e) {
                throw new RuntimeException("Error reading polygon: " + polygonWkt, e);
            }
        }
        MultiPolygon multiPolygon = geometryFactory.createMultiPolygon(polygons.toArray(new Polygon[0]));
        if (operation.equals("delete") || operation.equals("delete-all")) {
            Set<PositionFix> speedFilterPositionFixes = null;
            if (maxSpeed != null) {
                speedFilterPositionFixes = new HashSet<PositionFix>();
                SearchQuery searchQuery = new SearchQuery();
                searchQuery.setProject(project);
                searchQuery.setFromDate(fromDate);
                searchQuery.setToDate(toDate);
                searchQuery.setAnimalIds(animalIds);
                List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
                RServeInterface rServeInterface = new RServeInterface(rServeConnectionPool);
                Map<Long, Set<Date>> animalDates = rServeInterface.runSpeedFilter(project, positionFixList, maxSpeed);
                for (PositionFix positionFix : positionFixList) {
                    Set<Date> dates = animalDates.get(positionFix.getAnimal().getId());
                    // Need to create new java.util.Date here because positionFix.detectionTime is a java.sql.Timestamp.
                    // Date and Timestamp have same hashCode but their equals methods differ, breaking contains call.
                    if ((dates != null) && dates.contains(new Date(positionFix.getDetectionTime().getTime()))) {
                        speedFilterPositionFixes.add(positionFix);
                    }
                }
            }
            int numDeleted =
                operation.equals("delete-all")
                ? positionFixDao.setDeletedOnOverlappingPositionFixes(project, fromDate, toDate, animalIds, speedFilterPositionFixes, null, true)
                : positionFixDao.setDeletedOnOverlappingPositionFixes(project, fromDate, toDate, animalIds, speedFilterPositionFixes, multiPolygon, true);
            positionFixDao.renumberPositionFixes(project);
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <num-deleted>" + numDeleted + "</num-deleted>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(200);
            return;
        }
        else if (operation.equals("undelete") || operation.equals("undelete-all")) {
            int numUndeleted =
                operation.equals("undelete-all")
                ? positionFixDao.setDeletedOnOverlappingPositionFixes(project, fromDate, toDate, animalIds, null, null, false)
                : positionFixDao.setDeletedOnOverlappingPositionFixes(project, fromDate, toDate, animalIds, null, multiPolygon, false);
            positionFixDao.renumberPositionFixes(project);
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <num-undeleted>" + numUndeleted + "</num-undeleted>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(200);
            return;
        }
        else {
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <error>" + "Unknown operation: " + operation + "</error>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(400);
            return;
        }
    }
}

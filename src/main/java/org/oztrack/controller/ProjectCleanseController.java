package org.oztrack.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.factory.Hints;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
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
    
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private PositionFixDao positionFixDao;

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
    public String getCleanseView(@ModelAttribute(value="project") Project project) {
        return "project-cleanse";
    }
    
    @RequestMapping(value="/projects/{id}/cleanse", method=RequestMethod.POST, produces="application/xml")
    @PreAuthorize("hasPermission(#project, 'write')")
    public void processCleanse(
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="operation", defaultValue="delete") String operation,
        HttpServletRequest request,
        HttpServletResponse response
    ) throws IOException {
        if (operation.equals("delete")) {
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
            int numDeleted = positionFixDao.deleteOverlappingPositionFixes(project, multiPolygon);
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <num-deleted>" + numDeleted + "</num-deleted>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(200);
        }
        else if (operation.equals("undelete")) {
            int numUndeleted = positionFixDao.undeleteAllPositionFixes(project);
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <num-undeleted>" + numUndeleted + "</num-undeleted>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(200);
        }
        else {
            PrintWriter out = response.getWriter();
            out.append("<?xml version=\"1.0\"?>\n");
            out.append("<cleanse-response xmlns=\"http://oztrack.org/xmlns#\">\n");
            out.append("    <error>" + "Unknown operation: " + operation + "</error>\n");
            out.append("</cleanse-response>\n");
            response.setStatus(400);
        }
    }
}

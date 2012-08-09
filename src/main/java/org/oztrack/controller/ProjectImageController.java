package org.oztrack.controller;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContext;
import org.geotools.referencing.CRS;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.util.MapUtils;
import org.oztrack.view.AnimalDetectionsFeatureBuilder;
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

@Controller
public class ProjectImageController {
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
    
    @RequestMapping(value="/projects/{id}/image", method=RequestMethod.GET, produces="image/png")
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public void getView(HttpServletResponse response, Model model, @ModelAttribute(value="project") Project project) throws Exception {
        ReferencedEnvelope mapBounds = new ReferencedEnvelope(project.getBoundingBox().getEnvelopeInternal(), CRS.decode("EPSG:" + 4326));
        Dimension mapDimension = MapUtils.calculateMapDimension(mapBounds, 600);
        
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(project);
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        SimpleFeatureCollection featureCollection = new AnimalDetectionsFeatureBuilder(positionFixList).buildFeatureCollection();
        FeatureLayer featureLayer = new FeatureLayer(featureCollection, MapUtils.buildPointStyle("Cross", 8, new Color(0x6Db3a7), 0.8));

        MapContext mapContext = new DefaultMapContext();
        mapContext.setAreaOfInterest(mapBounds);
        mapContext.addLayer(featureLayer);
        MapUtils.writePng(mapContext, mapDimension, response.getOutputStream());
        mapContext.dispose();
    }
}
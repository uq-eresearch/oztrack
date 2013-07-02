package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.util.ShpUtils;
import org.oztrack.view.AnimalDetectionsFeatureBuilder;
import org.oztrack.view.AnimalTrajectoryFeatureBuilder;
import org.oztrack.view.DetectionsKMLView;
import org.oztrack.view.TrajectoryKMLView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractView;

@Controller
public class TracksController {
    protected final Log logger = LogFactory.getLog(getClass());

    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private AnimalDao animalDao;

    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("project")
    public Project getProject(@RequestParam(value="projectId") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @InitBinder
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "fromDate",
            "toDate",
            "animalIds"
        );
        binder.registerCustomEditor(Date.class, new CustomDateEditor(isoDateFormat, true));
    }

    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery() {
        SearchQuery searchQuery = new SearchQuery();
        return searchQuery;
    }

    @RequestMapping(
        value="/{path:detections|trajectory}",
        method=RequestMethod.GET,
        produces={
            "application/vnd.google-earth.kml+xml",
            "application/zip"
        }
    )
    @PreAuthorize("hasPermission(#project, 'read')")
    public View getView(
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        @PathVariable(value="path") String path,
        @RequestParam(value="format", defaultValue="kml") String format
    ) throws Exception {
        searchQuery.setProject(project);
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        if (format.equals("kml")) {
            List<Animal> animals = animalDao.getAnimalsById(searchQuery.getAnimalIds());
            return
                path.equals("detections") ? new DetectionsKMLView(configuration, animals, positionFixList) :
                path.equals("trajectory") ? new TrajectoryKMLView(configuration, animals, positionFixList) :
                null;
        }
        else if (format.equals("shp")) {
            final String baseFileName = path;
            final SimpleFeatureCollection featureCollection =
                path.equals("detections") ? (new AnimalDetectionsFeatureBuilder(positionFixList, false)).buildFeatureCollection() :
                path.equals("trajectory") ? (new AnimalTrajectoryFeatureBuilder(positionFixList)).buildFeatureCollection() :
                null;
            return new AbstractView() {
                @Override
                protected void renderMergedOutputModel(
                    Map<String, Object> model,
                    HttpServletRequest request,
                    HttpServletResponse response
                )
                throws Exception {
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + baseFileName + ".zip\"");
                    response.setContentType("application/zip");
                    response.setCharacterEncoding("UTF-8");
                    ShpUtils.writeShpZip(featureCollection, baseFileName, response.getOutputStream());
                }
            };
        }
        else {
            return null;
        }
    }
}

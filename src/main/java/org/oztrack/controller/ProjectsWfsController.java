package org.oztrack.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.Range;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.view.ProjectsFeatureBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vividsolutions.jts.geom.Point;

@Controller
public class ProjectsWfsController {
    @Autowired
    private ProjectDao projectDao;

    @RequestMapping(value="/projectsWFS", method=RequestMethod.POST)
    @PreAuthorize("permitAll")
    public void handleProjectsWFS(HttpServletResponse response) {
        List<Project> projects = projectDao.getAll();
        HashMap<Long, Range<Date>> projectDetectionDateRangeMap = projectDao.getProjectDetectionDateRanges(false);
        HashMap<Long, Point> projectCentroidMap = projectDao.getProjectCentroids(false);
        SimpleFeatureCollection featureCollection = new ProjectsFeatureBuilder(
            projects,
            projectDetectionDateRangeMap,
            projectCentroidMap
        ).buildFeatureCollection();
        WfsControllerUtils.writeWfsResponse(response, featureCollection);
    }
}

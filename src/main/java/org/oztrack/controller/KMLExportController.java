package org.oztrack.controller;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.KMLExportView;
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
public class KMLExportController {
    protected final Log logger = LogFactory.getLog(getClass());

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

    @InitBinder("animal")
    public void initAnimalBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @ModelAttribute("animal")
    public Animal getAnimal(@RequestParam(value="animalId") Long animalId) {
        return animalDao.getAnimalById(animalId);
    }

    @RequestMapping(value="/exportKML", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public View handleRequest(
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="animal") Animal animal
    ) throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(project);
        searchQuery.setAnimalIds(Arrays.asList(animal.getId()));
        List<PositionFix> positionFixList = positionFixDao.getProjectPositionFixList(searchQuery);
        return new KMLExportView(project, animal, positionFixList);
    }
}

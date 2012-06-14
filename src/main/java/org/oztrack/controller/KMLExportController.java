package org.oztrack.controller;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.KMLExportView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    
    @RequestMapping(value="/exportKML", method=RequestMethod.GET)
    public View handleRequest(
        Model model,
        @RequestParam(value="projectId", required=false) String projectId,
        @RequestParam(value="animalId", required=false) String animalId
    ) throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        if ((projectId != null) && (animalId != null))  {
            logger.debug("for projectId: " + projectId);
            
            Project project = projectDao.getProjectById(Long.valueOf(projectId));
            searchQuery.setProject(project);
            
            Animal animal = animalDao.getAnimalById(Long.valueOf(animalId));
            ArrayList<Animal> animalList = new ArrayList<Animal>(1);
            animalList.add(animal);
            searchQuery.setAnimalList(animalList);
        }
        else {
            logger.debug("no projectId or queryType");
        }
        model.addAttribute("searchQuery", searchQuery);
        return new KMLExportView();
    }
}
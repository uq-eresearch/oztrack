package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.Page;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.validator.SearchFormValidator;
import org.oztrack.view.SearchQueryXLSView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.View;

@Controller
public class SearchController {
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private PositionFixDao positionFixDao;
    
    @Autowired
    AnimalDao animalDao;

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }
    
    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(HttpSession session, @ModelAttribute(value="project") Project project) {
        // If we have a SearchQuery instance in the session from a previously posted form,
        // replace the default SearchQuery instance in the model so we show previous values in the form.
        SearchQuery searchQuery = (SearchQuery) session.getAttribute("searchQuery");
        if (searchQuery == null) {
            searchQuery = new SearchQuery();
            searchQuery.setProject(project);
        }
        return searchQuery;
    }
    
    @RequestMapping(value="/projects/{id}/search", method=RequestMethod.POST)
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public String onSubmit(
        HttpSession session,
        Model model,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        BindingResult bindingResult
    ) throws Exception {
        new SearchFormValidator().validate(searchQuery, bindingResult);
        if (bindingResult.hasErrors()) {
            return "searchquery";
        }
        session.setAttribute("searchQuery", searchQuery);
        return showFormInternal(model, project, searchQuery, 0);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
        binder.registerCustomEditor(List.class, "animalList", new CustomCollectionEditor(List.class) {
            @Override
            protected Object convertElement(Object element) {
                String animalId = (String) element;
                Animal animal = new Animal();
                animal.setId(Long.valueOf(animalId));
                return animal;
            }
        });
    }
    
    @RequestMapping(value="/projects/{id}/search", method=RequestMethod.GET)
    @PreAuthorize("#project.global or hasPermission(#project, 'read')")
    public String showForm(
        Model model,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        @RequestParam(value="offset", defaultValue="0") int offset
    ) throws Exception {
        return showFormInternal(model, project, searchQuery, offset);
    }

    @RequestMapping(value="/projects/{id}/export", method=RequestMethod.GET)
    @PreAuthorize("#searchQuery.project.global or hasPermission(#searchQuery.project, 'read')")
    public View handleRequest(@ModelAttribute(value="searchQuery") SearchQuery searchQuery, Model model) throws Exception {
        model.addAttribute(SearchQueryXLSView.SEARCH_QUERY_KEY, searchQuery);
        return new SearchQueryXLSView(positionFixDao);
    }
    
    private String showFormInternal(
        Model model,
        Project project,
        SearchQuery searchQuery,
        int offset
    ) throws Exception {
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());

        int nbrObjectsPerPage=30;
        int totalCount=0;
        int nbrObjectsThisPage=0;

        switch (project.getProjectType()) {
             case GPS:
                 Page<PositionFix> positionFixPage = positionFixDao.getPage(searchQuery, offset, nbrObjectsPerPage);
                 nbrObjectsThisPage = positionFixPage.getObjects().size();
                 totalCount = positionFixPage.getCount();
                 model.addAttribute("positionFixList", positionFixPage.getObjects());
                 break;
             default:
                 break;
        }

        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("offset", offset);
        model.addAttribute("nbrObjectsPerPage", nbrObjectsPerPage);
        model.addAttribute("nbrObjectsThisPage", nbrObjectsThisPage);
        model.addAttribute("totalCount", totalCount);
        return "project-search";
    }
}

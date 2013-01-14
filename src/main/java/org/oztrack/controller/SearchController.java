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

    @InitBinder("project")
    public void initProjectBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }

    @InitBinder
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields(
            "fromDate",
            "toDate",
            "animalIds",
            "sortField"
        );
        binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
    }

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
    @PreAuthorize("hasPermission(#project, 'read')")
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

    @RequestMapping(value="/projects/{id}/search", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public String showForm(
        Model model,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        @RequestParam(value="offset", defaultValue="0") int offset
    ) throws Exception {
        return showFormInternal(model, project, searchQuery, offset);
    }

    @RequestMapping(value="/projects/{id}/export", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#searchQuery.project, 'read')")
    public View handleRequest(@ModelAttribute(value="searchQuery") SearchQuery searchQuery, Model model) throws Exception {
        List<PositionFix> positionFixes = positionFixDao.getProjectPositionFixList(searchQuery);
        return new SearchQueryXLSView(searchQuery.getProject(), positionFixes);
    }

    private String showFormInternal(
        Model model,
        Project project,
        SearchQuery searchQuery,
        int offset
    ) throws Exception {
        List<Animal> projectAnimalsList = animalDao.getAnimalsByProjectId(project.getId());
        Page<PositionFix> positionFixPage = positionFixDao.getPage(searchQuery, offset, 30);
        model.addAttribute("positionFixList", positionFixPage.getObjects());
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("offset", offset);
        model.addAttribute("nbrObjectsPerPage", positionFixPage.getLimit());
        model.addAttribute("nbrObjectsThisPage", positionFixPage.getObjects().size());
        model.addAttribute("totalCount", positionFixPage.getCount());
        return "project-search";
    }
}

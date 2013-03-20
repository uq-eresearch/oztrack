package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.Page;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.PositionFix;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.SearchQueryXLSView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
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
import org.springframework.web.servlet.View;

import au.com.bytecode.opencsv.CSVWriter;

@Controller
public class SearchController {
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

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
        binder.registerCustomEditor(Date.class, new CustomDateEditor(isoDateFormat, true));
    }

    @ModelAttribute("project")
    public Project getProject(@PathVariable(value="id") Long projectId) {
        return projectDao.getProjectById(projectId);
    }

    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(@ModelAttribute(value="project") Project project) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(project);
        return searchQuery;
    }

    @RequestMapping(value="/projects/{id}/search", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#project, 'read')")
    public String showForm(
        Model model,
        @ModelAttribute(value="project") Project project,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        @RequestParam(value="offset", defaultValue="0") int offset
    ) throws Exception {
        StringBuilder searchQueryParams = new StringBuilder();
        searchQueryParams.append("fromDate=" + ((searchQuery.getFromDate() == null) ? "" : isoDateFormat.format(searchQuery.getFromDate())));
        searchQueryParams.append("&toDate=" + ((searchQuery.getToDate() == null) ? "" : isoDateFormat.format(searchQuery.getToDate())));
        for (Long animalId : searchQuery.getAnimalIds()) {
            searchQueryParams.append("&animalIds=" + animalId);
        }
        searchQueryParams.append("&sortField=" + searchQuery.getSortField());
        model.addAttribute("searchQueryParams", searchQueryParams.toString());
        return showFormInternal(model, project, searchQuery, offset);
    }

    @RequestMapping(value="/projects/{id}/export", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#searchQuery.project, 'read')")
    public View handleRequest(
        Model model,
        @ModelAttribute(value="searchQuery") SearchQuery searchQuery,
        @RequestParam(value="format", defaultValue="xls") String format
    ) throws Exception {
        final List<PositionFix> positionFixes = positionFixDao.getProjectPositionFixList(searchQuery);
        if (format.equals("xls")) {
            return new SearchQueryXLSView(searchQuery.getProject(), positionFixes);
        }
        else {
            return new View() {
                @Override
                public String getContentType() {
                    return "text/csv";
                }

                @Override
                public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                    response.setHeader("Content-Disposition", "attachment; filename=\"export.csv\"");
                    CSVWriter writer = new CSVWriter(response.getWriter());
                    try {
                        writer.writeNext(new String[] {"ANIMALID", "DATE", "LONGITUDE", "LATITUDE"});
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        for (PositionFix positionFix : positionFixes) {
                            writer.writeNext(new String[] {
                                String.valueOf(positionFix.getAnimal().getId()),
                                dateFormat.format(positionFix.getDetectionTime()),
                                String.valueOf(positionFix.getLocationGeometry().getX()),
                                String.valueOf(positionFix.getLocationGeometry().getY())
                            });
                        }
                    }
                    finally {
                        try {writer.close();} catch (Exception e) {}
                    }
                }
            };
        }
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
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, false));
        model.addAttribute("offset", offset);
        model.addAttribute("nbrObjectsPerPage", positionFixPage.getLimit());
        model.addAttribute("nbrObjectsThisPage", positionFixPage.getObjects().size());
        model.addAttribute("totalCount", positionFixPage.getCount());
        return "project-search";
    }
}

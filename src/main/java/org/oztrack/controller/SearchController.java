package org.oztrack.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.security.core.Authentication;
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

    @Autowired
    private OzTrackPermissionEvaluator permissionEvaluator;

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
    public SearchQuery getSearchQuery(
        Authentication authentication,
        @ModelAttribute(value="project") Project project,
        @RequestParam(value="includeDeleted", required=false) Boolean includeDeleted
    ) {
        SearchQuery searchQuery = new SearchQuery();
        searchQuery.setProject(project);
        if (permissionEvaluator.hasPermission(authentication, project, "write")) {
            searchQuery.setIncludeDeleted(includeDeleted);
        }
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
        return showFormInternal(model, project, searchQuery, offset);
    }

    @RequestMapping(value="/projects/{id}/export", method=RequestMethod.GET)
    @PreAuthorize("hasPermission(#searchQuery.project, 'read')")
    public View handleRequest(
        Model model,
        @ModelAttribute(value="searchQuery") final SearchQuery searchQuery,
        @RequestParam(value="format", defaultValue="xls") String format
    ) throws Exception {
        final List<PositionFix> positionFixes = positionFixDao.getProjectPositionFixList(searchQuery);
        if (format.equals("xls")) {
            return new SearchQueryXLSView(searchQuery.getProject(), positionFixes, (searchQuery.getIncludeDeleted() != null) && searchQuery.getIncludeDeleted());
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
                        ArrayList<String> headerLine = new ArrayList<String>();
                        headerLine.add("ANIMALID");
                        headerLine.add("DATE");
                        headerLine.add("LONGITUDE");
                        headerLine.add("LATITUDE");
                        if ((searchQuery.getIncludeDeleted() != null) && searchQuery.getIncludeDeleted()) {
                            headerLine.add("DELETED");
                        }
                        writer.writeNext(headerLine.toArray(new String[] {}));
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        for (PositionFix positionFix : positionFixes) {
                            ArrayList<String> valuesLine = new ArrayList<String>();
                            valuesLine.add(positionFix.getAnimal().getProjectAnimalId());
                            valuesLine.add(dateFormat.format(positionFix.getDetectionTime()));
                            valuesLine.add(String.valueOf(positionFix.getLocationGeometry().getX()));
                            valuesLine.add(String.valueOf(positionFix.getLocationGeometry().getY()));
                            if ((searchQuery.getIncludeDeleted() != null) && searchQuery.getIncludeDeleted()) {
                                valuesLine.add(((positionFix.getDeleted() != null) && positionFix.getDeleted()) ? "TRUE" : "FALSE");
                            }
                            writer.writeNext(valuesLine.toArray(new String[] {}));
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
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("positionFixPage", positionFixPage);
        model.addAttribute("projectAnimalsList", projectAnimalsList);
        model.addAttribute("projectDetectionDateRange", projectDao.getDetectionDateRange(project, false));
        return "project-search";
    }
}

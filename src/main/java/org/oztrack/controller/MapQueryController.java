package org.oztrack.controller;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.access.PositionFixDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.oztrack.view.KMLMapQueryView;
import org.oztrack.view.WFSMapQueryView;
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
public class MapQueryController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Autowired
    private ProjectDao projectDao;
    
    @Autowired
    private PositionFixDao positionFixDao;

    @InitBinder("searchQuery")
    public void initSearchQueryBinder(WebDataBinder binder) {
        binder.setAllowedFields();
    }
    
    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(
        @RequestParam(value="projectId", required=false) Long projectId,
        @RequestParam(value="queryType", required=false) String queryType,
        @RequestParam(value="dateFrom", required=false) String dateFrom,
        @RequestParam(value="dateTo", required=false) String dateTo,
        @RequestParam(value="srs", required=false) String srs,
        @RequestParam(value="percent", required=false) Double percent,
        @RequestParam(value="h", required=false) String h,
        @RequestParam(value="alpha", required=false) Double alpha
    )
    throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        if (projectId != null) {
            Project project = projectDao.getProjectById(Long.valueOf(projectId));
            searchQuery.setProject(project);
        }
        if (queryType != null) {
            searchQuery.setMapQueryType(MapQueryType.valueOf(queryType));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (dateFrom != null) {
            searchQuery.setFromDate(sdf.parse(dateFrom));
        }
        if (dateTo != null) {
            searchQuery.setToDate(sdf.parse(dateTo));
        }
        if (srs != null && !srs.isEmpty()) {
            String regex = "[a-zA-Z_-]+:[0-9]+";
            if (!srs.matches(regex)) {
                throw new RuntimeException("Invalid SRS code: must match " + regex);
            }
            searchQuery.setSrs(srs);
        }
        if (percent != null && !percent.isNaN()) {
            searchQuery.setPercent(percent);
        }
        if (h != null && !h.isEmpty()) {
            boolean validDouble = false;
            try {
                Double.parseDouble(h);
                validDouble = true;
            }
            catch (NumberFormatException e) {
            }
            String regex = "href|LSCV";
            if (!h.matches(regex) && !validDouble) {
                throw new RuntimeException("Invalid h value: must be number or match " + regex);
            }
            searchQuery.setH(h);
        }
        if (alpha != null && !alpha.isNaN()) {
            searchQuery.setAlpha(alpha);
        }
        return searchQuery;
    }
    
    @RequestMapping(value="/mapQueryKML", method=RequestMethod.GET)
    @PreAuthorize("#searchQuery.project.global or hasPermission(#searchQuery.project, 'read')")
    public View handleKMLQuery(@ModelAttribute(value="searchQuery") SearchQuery searchQuery) throws Exception {
        return new KMLMapQueryView(positionFixDao);
    }
    
    @RequestMapping(value="/mapQueryWFS", method=RequestMethod.POST)
    @PreAuthorize(
        "(#searchQuery.mapQueryType == T(org.oztrack.data.model.types.MapQueryType).ALL_PROJECTS) or " +
        "#searchQuery.project.global or " +
        "hasPermission(#searchQuery.project, 'read')"
    )
    public View handleWFSQuery(@ModelAttribute(value="searchQuery") SearchQuery searchQuery) throws Exception {
        return new WFSMapQueryView(projectDao, positionFixDao);
    }
}
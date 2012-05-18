package org.oztrack.controller;

import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MapQueryController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @ModelAttribute("searchQuery")
    public SearchQuery getSearchQuery(
        @RequestParam(value="projectId", required=false) Long projectId,
        @RequestParam(value="queryType", required=false) String queryType,
        @RequestParam(value="dateFrom", required=false) String dateFrom,
        @RequestParam(value="dateTo", required=false) String dateTo,
        @RequestParam(value="percent", required=false) Double percent,
        @RequestParam(value="h", required=false) String h
    )
    throws Exception {
        SearchQuery searchQuery = new SearchQuery();
        if (projectId != null) {
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
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
        if (percent != null && !percent.isNaN()) {
            searchQuery.setPercent(percent);
        }
        if (h != null && !h.isEmpty()) {
            searchQuery.setH(h);
        }
        return searchQuery;
    }
    
    @RequestMapping(value="/mapQueryKML", method=RequestMethod.GET)
    public String handleKMLQuery() throws Exception {
        return "java_KMLMapQuery";
    }
    
    @RequestMapping(value="/mapQueryWFS", method=RequestMethod.POST)
    public String handleWFSQuery() throws Exception {
        return "java_WFSMapQuery";
    }
}
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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MapQueryController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @RequestMapping(value="/mapQueryKML", method=RequestMethod.GET)
    public String handleKMLQuery(
        Model model,
        @RequestParam(value="projectId", required=false) Long projectId,
        @RequestParam(value="queryType", required=false) String queryType,
        @RequestParam(value="dateFrom", required=false) String dateFrom,
        @RequestParam(value="dateTo", required=false) String dateTo
    )
    throws Exception {
        return handleQuery(
            model,
            projectId,
            queryType,
            dateFrom,
            dateTo,    
            "java_KMLMapQuery"
        );
    }
    
    @RequestMapping(value="/mapQueryWFS", method=RequestMethod.POST)
    public String handleWFSQuery(
        Model model,
        @RequestParam(value="projectId", required=false) Long projectId,
        @RequestParam(value="queryType", required=false) String queryType,
        @RequestParam(value="dateFrom", required=false) String dateFrom,
        @RequestParam(value="dateTo", required=false) String dateTo
    )
    throws Exception {
        return handleQuery(
            model,
            projectId,
            queryType,
            dateFrom,
            dateTo,    
            "java_WFSMapQuery"
        );
    }

    private String handleQuery(
        Model model,
        Long projectId,
        String queryType,
        String dateFrom,
        String dateTo,
        String viewName
    )
    throws Exception {
        SearchQuery searchQuery = new SearchQuery();

        if ((projectId != null) && (queryType != null)) {
            logger.debug("for projectId: " + projectId + " + queryType: " + queryType);
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            Project project = projectDao.getProjectById(Long.valueOf(projectId));
            searchQuery.setProject(project);
            searchQuery.setMapQueryType(MapQueryType.valueOf(queryType));
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if (dateFrom !=  null) {
                searchQuery.setFromDate(sdf.parse(dateFrom));
            }
            if (dateTo != null) {
                searchQuery.setToDate(sdf.parse(dateTo));
            }
        }
        else if ((projectId == null) && (queryType != null)) {
            searchQuery.setMapQueryType(MapQueryType.valueOf(queryType));
        }
        else {
            logger.debug("no projectId or queryType");
        }

        model.addAttribute("searchQuery", searchQuery);
        
        return viewName;
    }
}
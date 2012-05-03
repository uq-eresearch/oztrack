package org.oztrack.controller;

import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 4/08/11
 * Time: 11:38 AM
 */
public class WFSProjectMapController implements Controller {

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        /* parameters from OpenLayers HTTP request */
        String projectId = request.getParameter("projectId");
        String queryType = request.getParameter("queryType");
        String dateFrom = request.getParameter("dateFrom");
        String dateTo = request.getParameter("dateTo");
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
        	
        } else {
            
        	logger.debug("no projectId or queryType");
        }

        return new ModelAndView("java_WFSMapQuery","searchQuery", searchQuery);
    }



    /*
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

    }
    */
}

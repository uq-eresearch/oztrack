package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.MapQueryType;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 4/08/11
 * Time: 11:38 AM
 */
public class ProjectMapAjaxController extends SimpleFormController {

    /**
    * Logger for this class and subclasses
    */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        /* parameters from OpenLayers HTTP request */
        String projectId = request.getParameter("projectId");
        String queryType = request.getParameter("queryType");
        SearchQuery searchQuery = new SearchQuery();

        if ((projectId != null) && (queryType != null)) {
            logger.debug("AjaxController for projectId: " + projectId + " + queryType: " + queryType);
            ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
            Project project = projectDao.getProjectById(Long.valueOf(projectId));
            searchQuery.setProject(project);
            searchQuery.setSearchQueryType(MapQueryType.valueOf(queryType));
        }
        else {
            logger.debug("AjaxController no projectId or queryType");
        }

        return new ModelAndView("ajax_mapquery","searchQuery", searchQuery);
    }



    /*
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

    }
    */
}

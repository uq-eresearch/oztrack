package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.KmlLayer;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.data.model.types.SearchQueryType;
import org.springframework.validation.BindException;
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

        String projectId = request.getParameter("projectId");
        if (projectId != null) {
            logger.debug("AjaxController for projectId: " + projectId);
        }
        else {
            logger.debug("AjaxController no projectId");
        }

        SearchQueryType searchQueryType = SearchQueryType.valueOf(request.getParameter("queryType"));
        SearchQuery searchQuery = new SearchQuery(Long.valueOf(projectId),searchQueryType);
        KmlLayer kmlLayer = new KmlLayer(searchQuery);


        return new ModelAndView("ajax_mapquery","kmlLayer", kmlLayer);
    }



    /*
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

    }
    */
}

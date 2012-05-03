package org.oztrack.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.SearchQuery;
import org.oztrack.view.SearchQueryXLSView;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 16/08/11
 * Time: 10:47 AM
 */
public class SearchQueryExportController implements Controller {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map model = new HashMap();
        SearchQuery searchQuery = (SearchQuery) request.getSession().getAttribute("searchQuery");
        model.put(SearchQueryXLSView.SEARCH_QUERY_KEY, searchQuery);
        return new ModelAndView(new SearchQueryXLSView(), model);
    }
}

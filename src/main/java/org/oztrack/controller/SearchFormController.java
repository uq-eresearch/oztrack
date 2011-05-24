package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.SearchQuery;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 24/05/11
 * Time: 11:24 AM
  */
public class SearchFormController extends SimpleFormController {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        SearchQuery searchQuery = (SearchQuery) command;

        String testString = buildQuery(searchQuery);


        ModelAndView modelAndView = showForm(request, response, errors);
        modelAndView.addObject("testString", testString);
        return modelAndView;
    }

    protected String buildQuery(SearchQuery searchQuery) {

        String select = "SELECT * ";
        String from = " FROM acousticdetection ad";
        String where = " WHERE ";

        if (searchQuery.getProjectAnimalId() != null) {
            from = from + ", animal a";
            where = where + " ad.animal_id=a.id"
                          + " AND a.projectanimalid = '"
                          + searchQuery.getProjectAnimalId() + "'";

        }

        String sql = select + from + where;

        logger.debug(sql);
        return sql;

    }




}

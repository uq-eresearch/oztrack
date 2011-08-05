package org.oztrack.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
        return new ModelAndView("ajax_mapquery");
    }



    /*
    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

    }
    */
}

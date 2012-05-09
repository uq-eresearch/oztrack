package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.data.model.User;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class ContactController implements Controller {
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        User currentUser = (User) httpServletRequest.getSession().getAttribute(Constants.CURRENT_USER);

        ModelAndView modelAndView = new ModelAndView("contact");
        modelAndView.addObject(Constants.CURRENT_USER, currentUser);
        return modelAndView;
    }
}

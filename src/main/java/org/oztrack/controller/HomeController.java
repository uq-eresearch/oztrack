package org.oztrack.controller;

import org.oztrack.app.Constants;
import org.oztrack.data.model.User;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 18/05/11
 * Time: 1:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class HomeController implements Controller {

    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        User currentUser = (User) httpServletRequest.getSession().getAttribute(Constants.CURRENT_USER);

        ModelAndView modelAndView = new ModelAndView("Home");
        modelAndView.addObject(Constants.CURRENT_USER, currentUser);
        return modelAndView;

    }

}

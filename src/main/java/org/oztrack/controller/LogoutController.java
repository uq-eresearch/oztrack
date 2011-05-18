package org.oztrack.controller;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 18/05/11
 * Time: 3:00 PM
 */

public class LogoutController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response){
       ModelAndView modelAndView = new ModelAndView("home");
       request.getSession().invalidate();
       return modelAndView;

    }


}

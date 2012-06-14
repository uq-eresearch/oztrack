package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectListController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @RequestMapping(value="/projects", method=RequestMethod.GET)
    public String getView(HttpSession session, Model model) {
        Long currentUserId = (Long) session.getAttribute(Constants.CURRENT_USER_ID);
        if (currentUserId == null) {
            return "redirect:login";
        }
        return "projects";
    }
}
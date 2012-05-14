package org.oztrack.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProjectListController {
    protected final Log logger = LogFactory.getLog(getClass());
    
    @RequestMapping(value="/projects", method=RequestMethod.GET)
    public String getView(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute(Constants.CURRENT_USER);
        if (currentUser == null) {
        	return "redirect:login";
        }
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User user = userDao.getByUsername(currentUser.getUsername());
        userDao.refresh(user);
        model.addAttribute("user", user);
        return "projects";
    }
}
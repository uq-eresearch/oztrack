package org.oztrack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectUser;
import org.oztrack.data.model.User;
import org.oztrack.data.model.types.Role;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Author: alabri
 * Date: 14/03/11
 * Time: 5:22 PM
 */
public class UserListController implements Controller {

	/** Logger for this class and subclasses */
    protected final Log logger = LogFactory.getLog(getClass());
    
    @Override
    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User user = userDao.getByUsername("test");
        if (user == null) {
            user = new User("test");
            user.setEmail("test@email.com");
            user.setFirstName("Test");
            userDao.save(user);
        }
        
    	ProjectDao projectDao = OzTrackApplication.getApplicationContext().getDaoManager().getProjectDao();
    	Project project = projectDao.getProjectById((long) 1);

    	logger.debug("---------------Initproject---------------");
        
        if (project == null) {
        	project = new Project("Project1 Title");
//        	project.setContactGivenName("Fred");
//        	project.setContactFamilyName("Smith");
        	ProjectUser pu = new ProjectUser();
        	pu.setProject(project);
        	pu.setUser(user);
        	pu.setRole(Role.ADMIN);
        	
        	List <ProjectUser> projectUsers = project.getProjectUsers();
        	projectUsers.add(pu);
        	project.setProjectUsers(projectUsers);
    
//          	logger.debug("project " + project.getTitle() + " " + project.getContactFamilyName());
         	logger.debug("project users" + project.getProjectUsers());
            
        	projectDao.save(project);
        }
        
        List<User> allUsers = userDao.getAll();
        ModelAndView modelAndView = new ModelAndView("users");
        modelAndView.addObject("userList", allUsers);
        modelAndView.addObject("appTitle", OzTrackApplication.getApplicationContext().getApplicationTitle());
        modelAndView.addObject("version", OzTrackApplication.getApplicationContext().getVersion());
        return modelAndView;
    }
}

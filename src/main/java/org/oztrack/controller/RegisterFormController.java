package org.oztrack.controller;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.AnimalDao;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.Animal;
import org.oztrack.data.model.User;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import au.edu.uq.itee.maenad.util.BCrypt;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author uqpnewm5
 *
 */


public class RegisterFormController extends SimpleFormController {
	  
	 /** Logger for this class and subclasses */
     protected final Log logger = LogFactory.getLog(getClass());
	
     @Override
     protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

		User user = (User) command;
		
        String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		user.setPassword(passwordHash);
		
		UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
		userDao.save(user);
        logger.debug("register user: " + user.getUsername());

        ModelAndView modelAndView;
        
        if (request.getParameter("update") != null) { 
        	modelAndView = new ModelAndView("redirect:projects");
        } else {
        	modelAndView = new ModelAndView(getSuccessView());
        	modelAndView.addObject("user", user);
        }
       	return modelAndView;    

     }
	  
	  @Override
	  protected Object formBackingObject(HttpServletRequest request) throws Exception {

	      String username = request.getParameter("user");
	      User user = new User();

          if (username != null) {
              UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
              user = userDao.getByUsername(username);
          }

	      return user;
	  }

}	
	


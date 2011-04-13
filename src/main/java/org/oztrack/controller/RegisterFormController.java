package org.oztrack.controller;


import javax.servlet.ServletException;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
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
	  protected ModelAndView onSubmit(Object command) throws ServletException {
	    
		User user = (User) command;
		
        String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
		user.setPassword(passwordHash);
		
		UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
		userDao.save(user);
        logger.debug("register user: " + user.getUsername());
		
		ModelAndView modelAndView = new ModelAndView(getSuccessView());
	    modelAndView.addObject("user", user);

	    return modelAndView;    
	    }

}	
	


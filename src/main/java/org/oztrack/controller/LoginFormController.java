package org.oztrack.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.Constants;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;


/**
 * @author uqpnewm5
 */

public class LoginFormController extends SimpleFormController {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    
	@Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {

        User user = (User) command;
        logger.info("Login User: " + user.getUsername());

        // set user for this session
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User currentUser = userDao.getByUsername(user.getUsername());

        HttpSession session = request.getSession();
        session.setAttribute(Constants.CURRENT_USER, currentUser);

//        AuthenticationManager authenticationManager = OzTrackApplication.getApplicationContext().getAuthenticationManager();
//        authenticationManager.setUserSession(session, currentUser);

        ModelAndView modelAndView = new ModelAndView(getSuccessView());
        return modelAndView;
    }

}
	


package org.oztrack.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegisterFormValidator implements Validator {

	
	@Override
    public boolean supports(Class clazz) {
        return User.class.isAssignableFrom(clazz);
    }
	
	public void validate(Object obj, Errors errors) {
	      
		//User user = (User) obj;     

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.empty.field", "Please Enter User Name");
        User loginUser = (User) obj;
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User user = userDao.getByUsername(loginUser.getUsername());

        if (!errors.hasFieldErrors("username")) {
            
        	if ((user != null) && (user != loginUser)) {
                errors.rejectValue("username", "unavailable.user", "This username is unavailable. Please try another.");
            }
            else {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "error.empty.field", "Please Enter firstName");
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.empty.field", "Please Enter lastName");
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.empty.field", "Please Enter Password");
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.empty.field", "Please Enter email");
            }
        }
        
        if (!errors.hasFieldErrors("email")) {
        	try {
        		InternetAddress emailAddr = new InternetAddress(loginUser.getEmail());
        		emailAddr.validate();
        	} catch (AddressException ex) {
        		errors.rejectValue("email", "invalid.email", "Email error: " + ex.getMessage());
        	}
        }
        
	}
	
}	

package org.oztrack.validator;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class RegisterFormValidator implements Validator {
    private UserDao userDao;
    
    public RegisterFormValidator(UserDao userDao) {
        this.userDao = userDao;
    }
	
	@Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return User.class.isAssignableFrom(clazz);
    }
	
	public void validate(Object obj, Errors errors) {
	    User loginUser = (User) obj;
	    
	    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.empty.field", "Please enter username");
		
		User existingUserByUsername = userDao.getByUsername(loginUser.getUsername());
    	if ((existingUserByUsername != null) && (existingUserByUsername != loginUser)) {
            errors.rejectValue("username", "unavailable.user", "This username is unavailable. Please try another.");
        }
        
        if ((loginUser.getAafId() != null) && !loginUser.getAafId().trim().isEmpty()) {
            User existingUserByAafId = userDao.getByAafId(loginUser.getAafId());
            if ((existingUserByAafId != null) && (existingUserByAafId != loginUser)) {
                errors.rejectValue("aafId", "aafId.user", "This AAF ID is already associated with another account.");
            }
        }
        else {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.empty.field", "Please Enter Password");
        }

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "error.empty.field", "Please Enter firstName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "error.empty.field", "Please Enter lastName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "error.empty.field", "Please Enter email");
        
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
package org.oztrack.validator;

import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LoginFormValidator implements Validator {
    private UserDao userDao;
    
    public LoginFormValidator(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        User loginUser = (User) obj;
        User user = userDao.getByUsername(loginUser.getUsername());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.empty.field", "Please enter User Name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.empty.field", "Please Enter Password");
        if (errors.hasFieldErrors("username") || errors.hasFieldErrors("password")) {
            return;
        }
        if (user == null) {
            errors.rejectValue("username", "unknown.user", "Invalid username/password");
            return;
        }
        if (!(BCrypt.checkpw(loginUser.getPassword(), user.getPassword()))) {
            errors.rejectValue("password", "wrong.password", "Invalid username/password");
            return;
        }
    }
}	

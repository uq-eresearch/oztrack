package org.oztrack.validator;

import au.edu.uq.itee.maenad.util.BCrypt;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.access.UserDao;
import org.oztrack.data.model.User;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class LoginFormValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        User loginUser = (User) obj;
        UserDao userDao = OzTrackApplication.getApplicationContext().getDaoManager().getUserDao();
        User user = userDao.getByUsername(loginUser.getUsername());
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "error.empty.field", "Please enter User Name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "error.empty.field", "Please Enter Password");
        if (!errors.hasFieldErrors("username")) {
            if (user == null) {
                errors.rejectValue("username", "unknown.user", "Unknown User");
            } else {
                if (!errors.hasFieldErrors("password")) {
                    if (!(BCrypt.checkpw(loginUser.getPassword(), user.getPassword()))) {
                        errors.rejectValue("password", "wrong.password", "Wrong Password");
                    }
                }
            }
        }
    }
}	

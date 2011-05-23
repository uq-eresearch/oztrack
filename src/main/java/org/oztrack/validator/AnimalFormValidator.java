package org.oztrack.validator;

import org.oztrack.data.model.Animal;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 23/05/11
 * Time: 2:33 PM
 */

public class AnimalFormValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Animal.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {

        //User user = (User) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "animalName", "error.empty.field", "Please Enter");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "animalDescription", "error.empty.field", "Please Enter");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fileType", "error.empty.field", "Please Enter");

        /*TODO:
        1.
        2.
        */

    }


}

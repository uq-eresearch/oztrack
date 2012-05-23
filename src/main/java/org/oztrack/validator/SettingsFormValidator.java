package org.oztrack.validator;

import org.oztrack.data.model.Settings;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class SettingsFormValidator implements Validator {
    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return Settings.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "homeText", "error.empty.field", "Please enter text for the 'Home' page");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "aboutText", "error.empty.field", "Please enter text for the 'About' page");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactText", "error.empty.field", "Please enter text for the 'Contact' page");
    }
}

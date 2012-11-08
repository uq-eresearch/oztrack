package org.oztrack.validator;

import org.oztrack.data.model.Animal;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class AnimalFormValidator implements Validator {
    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return Animal.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAnimalId", "error.empty.field", "Please enter animal ID");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "animalName", "error.empty.field", "Please enter animal name");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "colour", "error.empty.field", "Please enter animal colour");
    }
}
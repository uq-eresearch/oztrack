package org.oztrack.validator;

import org.oztrack.data.model.Sighting;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 8/06/11
 * Time: 9:53 AM
 */
public class SightingFormValidator implements Validator {

    @Override
    public boolean supports(Class clazz) {
        return Sighting.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "sightingDate", "error.empty.field", "You must enter a date.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "localityDescription", "error.empty.field", "Please describe the location.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "latitude", "error.empty.field", "Latitude cannot be empty.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "longitude", "error.empty.field", "Longitude cannot be empty.");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "speciesCommonName", "error.empty.field", "Have a go.");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactName", "error.empty.field", "You must provide a contact for us.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactEmail", "error.empty.field", "Please provide a valid email address.");

    }

}
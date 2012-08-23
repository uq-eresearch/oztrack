package org.oztrack.validator;

import org.geotools.referencing.CRS;
import org.oztrack.data.model.Project;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ProjectFormValidator implements Validator {
    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return Project.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        Project project = (Project) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "error.empty.field", "Please enter a short project title.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.empty.field", "Please enter a description for the project.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "spatialCoverageDescr", "error.empty.field", "Please give a location description.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "rightsStatement", "error.empty.field", "The Rights Statement cannot be left empty.");
        try {
            CRS.decode(project.getSrsIdentifier());
        }
        catch (Exception e) {
            errors.rejectValue("srsIdentifier", "error.srsidentifier", "Please enter a valid SRS code.");
        }
    }
}
package org.oztrack.validator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.geotools.referencing.CRS;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.ProjectAccess;
import org.oztrack.util.EmbargoUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class ProjectFormValidator implements Validator {
    private SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return Project.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Project project = (Project) obj;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "error.empty.field", "Please enter a short project title.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.empty.field", "Please enter a description for the project.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "spatialCoverageDescr", "error.empty.field", "Please give a location description.");
        if (OzTrackApplication.getApplicationContext().isDataLicencingEnabled()) {
            if (project.getAccess() == ProjectAccess.OPEN) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dataLicence", "error.empty.field", "A Data Licence must be selected for Open Access projects.");
            }
            else if (project.getAccess() == ProjectAccess.EMBARGO) {
                ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dataLicence", "error.empty.field", "A Data Licence must be selected for Delayed Open Access projects.");
            }
        }
        if (project.getAccess() == ProjectAccess.EMBARGO) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "embargoDate", "error.empty.field", "An embargo date must be selected for Delayed Open Access projects.");
            if (project.getEmbargoDate() != null) {
                if (project.getEmbargoDate().before(DateUtils.truncate(new Date(), Calendar.DATE))) {
                    errors.rejectValue("embargoDate", "error.embargoDate", "Embargo date must be today's date or later.");
                }

                Date createDate = (project.getCreateDate() != null) ? project.getCreateDate() : new Date();
                EmbargoUtils.EmbargoInfo embargoInfo = EmbargoUtils.getEmbargoInfo(createDate);
                if (project.getEmbargoDate().after(embargoInfo.getMaxEmbargoDate())) {
                    errors.rejectValue("embargoDate", "error.embargoDate", "Embargo date must be " + shortDateFormat.format(embargoInfo.getMaxEmbargoDate()) + " or earlier.");
                }
            }
        }
        try {
            CRS.decode(project.getSrsIdentifier());
        }
        catch (Exception e) {
            errors.rejectValue("srsIdentifier", "error.srsidentifier", "Please enter a valid SRS code.");
        }
    }
}
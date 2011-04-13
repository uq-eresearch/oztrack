package org.oztrack.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;

import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.Project;
import org.oztrack.data.access.ProjectDao;


public class ProjectFormValidator implements Validator {

	
	@Override
    public boolean supports(Class clazz) {
        return Project.class.isAssignableFrom(clazz);
    }
	
	public void validate(Object obj, Errors errors) {
	      
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "error.empty.field", "Please Enter Title");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.empty.field", "Please Enter description");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "organisationName", "error.empty.field", "Please Enter organisation");
		
	}
	
}	

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
	      
		Project project = (Project) obj;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "error.empty.field", "Please enter a short project title.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "error.empty.field", "Please enter a description for the project.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactGivenName", "error.empty.field", "Please enter a short project title.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "contactFamilyName", "error.empty.field", "Please enter a short project title.");
 
        String contentType = project.getImageFile().getContentType();
        
        if  (project.getImageFile().getSize() != 0) {
        	
        	if (!contentType.contains("image")) {
        		errors.rejectValue("imageFile", "bad.content", "This is not an image file (eg. .gif,.jpg,.png).");
        	} else {
        		if (project.getImageFile().getSize() > 2000000) {
                errors.rejectValue("imageFile", "big.file", "Your image file size is too big (max 2MB).");
        		}
        	}		
        }
        
	}
}	

package org.oztrack.validator;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 12/04/11
 * Time: 1:30 PM
 * To change this template use File | Settings | File Templates.
 */

import org.oztrack.data.model.DataFile;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class DataFileFormValidator  implements Validator {

	@Override
    public boolean supports(Class clazz) {
        return DataFile.class.isAssignableFrom(clazz);
    }

	public void validate(Object obj, Errors errors) {

		DataFile dataFile = (DataFile) obj;
		//ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userGivenFileName", "error.empty.field", "Please Enter");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fileDescription", "error.empty.field", "Please Enter");
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "fileType", "error.empty.field", "You must specify a file");

        if  (dataFile.getFile().getSize() == 0) {
        	errors.rejectValue("file", "no.file", "No file was uploaded. Have another try.");
        }

	}

}

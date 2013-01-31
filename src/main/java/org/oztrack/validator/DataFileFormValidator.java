package org.oztrack.validator;

import org.oztrack.data.model.DataFile;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class DataFileFormValidator  implements Validator {
    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return DataFile.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        DataFile dataFile = (DataFile) obj;
        if (dataFile.getFile().getSize() == 0) {
            errors.rejectValue("file", "no.file", "Please select a file");
        }
    }
}
package org.oztrack.validator;

import org.oztrack.data.model.SearchQuery;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 24/05/11
 * Time: 1:43 PM
 */
public class SearchFormValidator implements Validator {

    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return SearchQuery.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
        //ValidationUtils.rejectIfEmptyOrWhitespace(errors, "projectAnimalId", "error.empty.field", "Please Enter an animal id");
    }

}

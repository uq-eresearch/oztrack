package org.oztrack.validator;

import org.oztrack.data.model.SearchQuery;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class SearchFormValidator implements Validator {
    @Override
    public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
        return SearchQuery.class.isAssignableFrom(clazz);
    }

    public void validate(Object obj, Errors errors) {
    }
}

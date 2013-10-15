package org.oztrack.controller;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.CountryDao;
import org.oztrack.data.model.Country;

public class CountryPropertyEditor extends PropertyEditorSupport {
    private CountryDao countryDao;

    public CountryPropertyEditor(CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    @Override
    public String getAsText() {
        Country country = (Country) getValue();
        if (country == null) {
            return null;
        }
        return String.valueOf(country.getId());
    }
    @Override
    public void setAsText(String text) {
        if (StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }
        Long countryId = Long.valueOf(text);
        Country country = countryDao.getById(countryId);
        setValue(country);
    }
}

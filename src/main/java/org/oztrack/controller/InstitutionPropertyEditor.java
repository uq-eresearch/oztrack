package org.oztrack.controller;

import java.beans.PropertyEditorSupport;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.model.Institution;

public class InstitutionPropertyEditor extends PropertyEditorSupport {
    private InstitutionDao institutionDao;

    public InstitutionPropertyEditor(InstitutionDao institutionDao) {
        this.institutionDao = institutionDao;
    }

    @Override
    public String getAsText() {
        Institution institution = (Institution) getValue();
        if (institution == null) {
            return null;
        }
        return String.valueOf(institution.getId());
    }
    @Override
    public void setAsText(String text) {
        if (StringUtils.isBlank(text)) {
            setValue(null);
            return;
        }
        Long institutionId = Long.valueOf(text);
        Institution institution = institutionDao.getById(institutionId);
        setValue(institution);
    }
}

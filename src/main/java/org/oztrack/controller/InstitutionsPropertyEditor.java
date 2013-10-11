package org.oztrack.controller;

import java.beans.PropertyEditorSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.model.Institution;

public class InstitutionsPropertyEditor extends PropertyEditorSupport {
    private InstitutionDao institutionDao;

    public InstitutionsPropertyEditor(InstitutionDao institutionDao) {
        this.institutionDao = institutionDao;
    }

    @Override
    public String getAsText() {
        @SuppressWarnings("unchecked")
        List<Institution> institutions = (List<Institution>) getValue();
        if (institutions == null) {
            return "";
        }
        ArrayList<String> idStrings = new ArrayList<String>();
        for (Institution institution : institutions) {
            idStrings.add(String.valueOf(institution.getId()));
        }
        return StringUtils.join(idStrings, ",");
    }
    @Override
    public void setAsText(String text) {
        if (text == null) {
            setValue(Collections.<Institution>emptyList());
            return;
        }
        ArrayList<Institution> institutions = new ArrayList<Institution>();
        for (String idString : text.split(",")) {
            Long institutionId = Long.valueOf(idString);
            Institution institution = institutionDao.getById(institutionId);
            institutions.add(institution);
        }
        setValue(institutions);
    }
}
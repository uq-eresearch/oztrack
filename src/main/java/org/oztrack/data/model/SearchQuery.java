package org.oztrack.data.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class SearchQuery {
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private Project project;
    private Date fromDate;
    private Date toDate;
    private List<Long> animalIds;
    private Boolean includeDeleted;
    private String sortField;

    public SearchQuery() {
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public List<Long> getAnimalIds() {
        return animalIds;
    }

    public void setAnimalIds(List<Long> animalIds) {
        this.animalIds = animalIds;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getUrlParams() {
        List<String> params = new ArrayList<String>();
        if (getFromDate() != null) {
            params.add("fromDate=" + isoDateFormat.format(getFromDate()));
        }
        if (getToDate() != null) {
            params.add("toDate=" + isoDateFormat.format(getToDate()));
        }
        if (getAnimalIds() != null) {
            for (Long animalId : getAnimalIds()) {
                params.add("animalIds=" + animalId);
            }
        }
        if (getSortField() != null) {
            params.add("sortField=" + getSortField());
        }
        return StringUtils.join(params, "&");
    }
}
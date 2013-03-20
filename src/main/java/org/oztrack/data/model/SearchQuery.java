package org.oztrack.data.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
        StringBuilder searchQueryParams = new StringBuilder();
        if (getFromDate() != null) {
            searchQueryParams.append("fromDate=" + isoDateFormat.format(getFromDate()));
        }
        if (getToDate() != null) {
            searchQueryParams.append("&toDate=" + isoDateFormat.format(getToDate()));
        }
        for (Long animalId : getAnimalIds()) {
            searchQueryParams.append("&animalIds=" + animalId);
        }
        if (getSortField() != null) {
            searchQueryParams.append("&sortField=" + getSortField());
        }
        return searchQueryParams.toString();
    }
}
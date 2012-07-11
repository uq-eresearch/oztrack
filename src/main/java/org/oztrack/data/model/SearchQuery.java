package org.oztrack.data.model;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.types.MapQueryType;

public class SearchQuery {
    protected final Log logger = LogFactory.getLog(getClass());

    private Date fromDate;
    private Date toDate;
    private String projectAnimalId;
    private String sortField;
    private List<Animal> animalList;
    private String [] speciesList;
    private Project project;
    private Boolean deleted;
    private MapQueryType mapQueryType;
    private String srs;
    private Double percent;
    private String h;
    private Double alpha;

    public SearchQuery() {
        this.fromDate = null;
        this.toDate = null;
        this.projectAnimalId = "";
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

    public String getProjectAnimalId() {
        return projectAnimalId;
    }

    public void setProjectAnimalId(String projectAnimalId) {
        this.projectAnimalId = projectAnimalId;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public List<Animal> getAnimalList() {
        return animalList;
    }

    public void setAnimalList(List<Animal> animalList) {
        this.animalList = animalList;
    }

    public String[] getSpeciesList() {
        return speciesList;
    }

    public void setSpeciesList(String[] speciesList) {
        this.speciesList = speciesList;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public MapQueryType getMapQueryType() {
        return mapQueryType;
    }

    public void setMapQueryType(MapQueryType mapQueryType) {
        this.mapQueryType = mapQueryType;
    }

    public String getSrs() {
        return srs;
    }

    public void setSrs(String srs) {
        this.srs = srs;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }

    public Double getAlpha() {
        return alpha;
    }

    public void setAlpha(Double alpha) {
        this.alpha = alpha;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }
}
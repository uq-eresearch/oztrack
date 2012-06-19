package org.oztrack.data.model;

import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.data.model.types.MapQueryType;

/**
 * Created by IntelliJ IDEA.
 * User: uqpnewm5
 * Date: 24/05/11
 * Time: 12:27 PM
 */

public class SearchQuery {

    /**
     * Logger for this class and subclasses
     */
    protected final Log logger = LogFactory.getLog(getClass());

    private Date fromDate;
    private Date toDate;
    private String projectAnimalId;
    private String receiverOriginalId;
    private String sortField;
    private List<Animal> animalList;
    private String [] speciesList;
    private Project project;
    private MapQueryType mapQueryType;
    private Double percent;
    private String h;
    private Double alpha;

    public SearchQuery() {
        this.fromDate = null;
        this.toDate = null;
        this.projectAnimalId = "";
        this.receiverOriginalId = "";
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

    public String getReceiverOriginalId() {
        return receiverOriginalId;
    }

    public void setReceiverOriginalId(String receiverOriginalId) {
        this.receiverOriginalId = receiverOriginalId;
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

    public MapQueryType getMapQueryType() {
        return mapQueryType;
    }

    public void setMapQueryType(MapQueryType mapQueryType) {
        this.mapQueryType = mapQueryType;
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

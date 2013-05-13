package org.oztrack.data.model.types;

import java.util.Date;

public class PositionFixStats {
    private Long animalId;
    private Date startDate;
    private Date endDate;
    private Long count;
    private Double dailyMean;
    private Long dailyMax;

    public PositionFixStats() {
    }

    public Long getAnimalId() {
        return animalId;
    }

    public void setAnimalId(Long animalId) {
        this.animalId = animalId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getDailyMean() {
        return dailyMean;
    }

    public void setDailyMean(Double dailyMean) {
        this.dailyMean = dailyMean;
    }

    public Long getDailyMax() {
        return dailyMax;
    }

    public void setDailyMax(Long dailyMax) {
        this.dailyMax = dailyMax;
    }
}

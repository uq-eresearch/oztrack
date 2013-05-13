package org.oztrack.data.model.types;

import java.util.Date;

public class TrajectoryStats {
    private Long animalId;
    private Date startDate;
    private Date endDate;
    private Double distance;
    private Double meanStepDistance;
    private Double meanStepSpeed;

    public TrajectoryStats() {
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

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getMeanStepDistance() {
        return meanStepDistance;
    }

    public void setMeanStepDistance(Double meanStepDistance) {
        this.meanStepDistance = meanStepDistance;
    }

    public Double getMeanStepSpeed() {
        return meanStepSpeed;
    }

    public void setMeanStepSpeed(Double meanStepSpeed) {
        this.meanStepSpeed = meanStepSpeed;
    }
}

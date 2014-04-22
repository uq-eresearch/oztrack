package org.oztrack.data.model.types;

import java.util.Date;

public class ProjectVisitSummary {
    private final Long numVisits;
    private final Date earliestDate;

    public ProjectVisitSummary(Long numVisits, Date earliestDate) {
        this.numVisits = numVisits;
        this.earliestDate = earliestDate;
    }

    public Long getNumVisits() {
        return numVisits;
    }

    public Date getEarliestDate() {
        return earliestDate;
    }
}
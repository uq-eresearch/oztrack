package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="analysis_parameter", uniqueConstraints=@UniqueConstraint(columnNames={"analysis_id", "name"}))
public class AnalysisParameter {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="analysis_parameter_id_seq")
    @SequenceGenerator(name="analysis_parameter_id_seq", sequenceName="analysis_parameter_id_seq",allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="analysis_id", nullable=false)
    private Analysis analysis;

    @Column(name="name", nullable=false, columnDefinition="text")
    private String name;

    @Column(name="value", nullable=false, columnDefinition="text")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
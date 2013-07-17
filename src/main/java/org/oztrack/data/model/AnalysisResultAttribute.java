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
@Table(name="analysis_result_attribute", uniqueConstraints=@UniqueConstraint(columnNames={"feature_id", "name"}))
public class AnalysisResultAttribute {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="analysis_result_attribute_id_seq")
    @SequenceGenerator(name="analysis_result_attribute_id_seq", sequenceName="analysis_result_attribute_id_seq", allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="analysis_id", nullable=true)
    private Analysis analysis;

    @ManyToOne
    @JoinColumn(name="feature_id", nullable=true)
    private AnalysisResultFeature feature;

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

    public AnalysisResultFeature getFeature() {
        return feature;
    }

    public void setFeature(AnalysisResultFeature feature) {
        this.feature = feature;
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
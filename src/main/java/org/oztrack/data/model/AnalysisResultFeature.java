package org.oztrack.data.model;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.oztrack.data.model.types.AnalysisResultAttributeType;

import com.vividsolutions.jts.geom.MultiPolygon;

@Entity
@Table(name="analysis_result_feature", uniqueConstraints=@UniqueConstraint(columnNames={"analysis_id", "animal_id"}))
public class AnalysisResultFeature {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="analysis_result_feature_id_seq")
    @SequenceGenerator(name="analysis_result_feature_id_seq", sequenceName="analysis_result_feature_id_seq", allocationSize=1)
    @Column(nullable=false)
    private Long id;

    @ManyToOne
    @JoinColumn(name="analysis_id", nullable=false)
    private Analysis analysis;

    @ManyToOne
    @JoinColumn(name="animal_id", nullable=false)
    private Animal animal;

    @Column(name="the_geom", columnDefinition="GEOMETRY")
    @Type(type="org.hibernatespatial.GeometryUserType")
    private MultiPolygon geometry;

    @OneToMany(mappedBy="feature", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    private Set<AnalysisResultAttribute> attributes;

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

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public MultiPolygon getGeometry() {
        return geometry;
    }

    public void setGeometry(MultiPolygon geometry) {
        this.geometry = geometry;
    }

    public Set<AnalysisResultAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<AnalysisResultAttribute> attributes) {
        this.attributes = attributes;
    }

    public AnalysisResultAttribute getAttribute(String name) {
        for (AnalysisResultAttribute attribute : attributes) {
            if (attribute.getName().equals(name)) {
                return attribute;
            }
        }
        return null;
    }

    public Object getResultAttributeValue(String name) {
        AnalysisResultAttributeType resultAttributeType = analysis.getAnalysisType().getResultAttributeType(name);
        AnalysisResultAttribute resultAttribute = getAttribute(resultAttributeType.getIdentifier());
        if (resultAttribute == null) {
            return null;
        }
        String stringValue = StringUtils.isNotBlank(resultAttribute.getValue()) ? resultAttribute.getValue() : null;
        return
            (stringValue == null) ? null
            : resultAttributeType.getDataType().equals("double") ? Double.valueOf(stringValue)
            : resultAttributeType.getDataType().equals("boolean") ?  Boolean.valueOf(stringValue)
            : stringValue;
    }
}
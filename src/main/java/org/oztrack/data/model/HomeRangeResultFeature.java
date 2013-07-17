package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiPolygon;

@Entity
@DiscriminatorValue("HOME_RANGE")
public class HomeRangeResultFeature extends AnalysisResultFeature {
    @Column(name="the_geom", columnDefinition="GEOMETRY")
    @Type(type="org.hibernatespatial.GeometryUserType")
    private MultiPolygon geometry;

    public MultiPolygon getGeometry() {
        return geometry;
    }

    public void setGeometry(MultiPolygon geometry) {
        this.geometry = geometry;
    }
}
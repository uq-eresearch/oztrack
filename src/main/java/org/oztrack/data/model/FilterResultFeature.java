package org.oztrack.data.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

@Entity
@DiscriminatorValue("FILTER")
public class FilterResultFeature extends AnalysisResultFeature {
    @Column(name="the_geom", columnDefinition="GEOMETRY")
    @Type(type="org.hibernatespatial.GeometryUserType")
    private Point geometry;

    public Point getGeometry() {
        return geometry;
    }

    public void setGeometry(Point geometry) {
        this.geometry = geometry;
    }
}

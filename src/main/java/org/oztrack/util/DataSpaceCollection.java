package org.oztrack.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.Range;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.oztrack.app.OzTrackApplication;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.ProjectAccess;
import org.springframework.core.io.ClassPathResource;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;

@SuppressWarnings("unused")
public class DataSpaceCollection {
    protected final Log logger = LogFactory.getLog(getClass());

    private static Template atomAgentTemplate;
    private static Template atomCollectionTemplate;

    private final Project project;
    private final Range<Date> dateRange;
    private final Polygon boundingBox;
    private String dataSpaceURL;

    private String collectionTitle;
    private String collectionDescription;
    private String collectionURL;
    private String contactGivenName;
    private String contactFamilyName;
    private String contactEmail;
    private String contactDescription;
    private String speciesCommonName;
    private String speciesScientificName;
    private String temporalCoverage;
    private String spatialCoverage;
    private String boundingBoxCoordinatesString;
    private String rightsStatement;
    private String accessRights;
    private String dataSpaceUpdateDate;
    private String dataSpaceAgentUpdateDate;
    private String contactDataSpaceURI;


    public DataSpaceCollection(Project project, Range<Date> dateRange, Polygon boundingBox) {
        this.project = project;
        this.dateRange = dateRange;
        this.boundingBox = boundingBox;
        buildAtomTemplates();
        this.dataSpaceURL = OzTrackApplication.getApplicationContext().getDataSpaceURL();
    }

    public static synchronized void buildAtomTemplates() {

        if (atomAgentTemplate != null)
            return;
        Reader templateReader;
        try {
            templateReader = new InputStreamReader((
                    new ClassPathResource("agent.mustache.atom")
                    ).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        atomAgentTemplate = Mustache.compiler().compile(templateReader);

        if (atomCollectionTemplate != null)
            return;
        //Reader templateReader;
        try {
            templateReader = new InputStreamReader((
                    new ClassPathResource("collection.mustache.atom")
                    ).getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        atomCollectionTemplate = Mustache.compiler().compile(templateReader);

    }

    public String agentToAtom() {
        return atomAgentTemplate.execute(this);
    }

    public String collectionToAtom() {
        return atomCollectionTemplate.execute(this);
    }

    public static Template getAtomAgentTemplate() {
        return atomAgentTemplate;
    }

    public static Template getAtomCollectionTemplate() {
        return atomCollectionTemplate;
    }

    public String getCollectionTitle() {
        return project.getTitle();
    }

    public String getCollectionDescription() {
        return project.getDescription();
    }

    public String getCollectionURL() {
        return "http://oztrack.org/projectdescr?id=" + project.getId().toString();
    }

    public String getContactGivenName() {
        return project.getDataSpaceAgent().getFirstName();
    }

    public String getContactFamilyName() {
        return project.getDataSpaceAgent().getLastName();
    }

    public String getContactEmail() {
        return project.getDataSpaceAgent().getEmail();
    }

    public String getContactDescription() {
        return project.getDataSpaceAgent().getDataSpaceAgentDescription();
    }

    public String getSpeciesCommonName() {
        return project.getSpeciesCommonName();
    }

    public String getSpeciesScientificName() {
        return project.getSpeciesScientificName();
    }

    public String getTemporalCoverage() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return "start=" + sdf.format(dateRange.getMinimum()) + "; end=" + sdf.format(dateRange.getMaximum());
    }

    public String getBoundingBoxCoordinatesString() {
        Coordinate[] coordinates = boundingBox.getCoordinates();
        String result = "";
        for (int i = 0; i < coordinates.length; i++) {
            result = result + " " + Double.toString(coordinates[i].y) + " " + Double.toString(coordinates[i].x);
        }
        return result;
    }

    public String getSpatialCoverage() {
        return project.getSpatialCoverageDescr();
    }

    public String getRightsStatement() {
        return project.getRightsStatement();
    }

    public String getAccessRights() {
        if (project.getAccess() == ProjectAccess.OPEN) {
            return "The data in this project are available in OzTrack for the public to use.";
        }
        else if (project.getAccess() == ProjectAccess.EMBARGO) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            return
                "The data in the project are covered by an embargo period, " +
                "ending " + dateFormat.format(project.getEmbargoDate()) + ", " +
                "and are currently only available to users on the OzTrack system whom have been granted access. " +
                "Contact the Collection Manager regarding permission and procedures for accessing the data.";
        }
        else {
            return
                "The data in this project are only available to users on the OzTrack system whom have been granted access. " +
                "Contact the Collection Manager regarding permission and procedures for accessing the data.";
        }
    }

    public String getDataSpaceUpdateDate() {
        Date d = project.getDataSpaceUpdateDate();
        if (d == null) d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(d);
    }

    public String getDataSpaceAgentUpdateDate() {
        Date d = project.getDataSpaceAgent().getDataSpaceAgentUpdateDate();
        if (d == null) d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(d);
    }

    public String getContactDataSpaceURL() {
        return this.dataSpaceURL + "agents/" + project.getDataSpaceAgent().getDataSpaceAgentURI();
    }



}

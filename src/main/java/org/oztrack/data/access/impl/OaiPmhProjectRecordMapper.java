package org.oztrack.data.access.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.OaiPmhEntityMapper;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectContribution;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.data.model.types.OaiPmhRecord.Relation;
import org.oztrack.data.model.types.OaiPmhRecord.Subject;
import org.oztrack.util.OaiPmhConstants;

import com.vividsolutions.jts.geom.Polygon;

public class OaiPmhProjectRecordMapper implements OaiPmhEntityMapper<Project, OaiPmhRecord> {
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private final OzTrackConfiguration configuration;
    private final Map<Long, Range<Date>> projectDetectionDateRanges;
    private final Map<Long, Polygon> projectBoundingBoxes;

    public OaiPmhProjectRecordMapper(
        OzTrackConfiguration configuration,
        Map<Long, Range<Date>> projectDetectionDateRanges,
        Map<Long, Polygon> projectBoundingBoxes
    ) {
        this.configuration = configuration;
        this.projectDetectionDateRanges = projectDetectionDateRanges;
        this.projectBoundingBoxes = projectBoundingBoxes;
    }

    @Override
    public OaiPmhRecord map(Project project) {
        OaiPmhRecord record = new OaiPmhRecord();
        String localIdentifier = "projects/" + project.getId();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + localIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + localIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + localIdentifier);
        record.setName(new OaiPmhRecord.Name(Arrays.asList(new OaiPmhRecord.Name.NamePart(null, project.getTitle()))));
        record.setDescription(project.getDescription());
        record.setUrl(configuration.getBaseUrl() + "/projects/" + project.getId());
        record.setCreator(null);
        record.setRecordCreateDate(project.getCreateDate());
        record.setRecordUpdateDate(project.getUpdateDate());
        record.setExistenceStartDate(project.getCreateDate());
        Range<Date> detectionDateRange = projectDetectionDateRanges.get(project.getId());
        if (detectionDateRange != null) {
            record.setTemporalCoverage(detectionDateRange);
        }
        Polygon boundingBox = projectBoundingBoxes.get(project.getId());
        if (boundingBox != null)  {
            record.setSpatialCoverage(boundingBox.getEnvelopeInternal());
        }
        switch (project.getAccess()) {
            case OPEN:
                record.setAccessRights("The data in this project are available for public use.");
                if (project.getDataLicence() != null) {
                    record.setLicence(new OaiPmhRecord.Licence(
                        project.getDataLicence().getIdentifier(),
                        project.getDataLicence().getInfoUrl(),
                        project.getDataLicence().getTitle()
                    ));
                }
                else {
                    record.setLicence(new OaiPmhRecord.Licence("NoLicense", null, null));
                }
                break;
            case EMBARGO:
                record.setAccessRights(
                    "The data in this project are under embargo until " +
                    isoDateFormat.format(project.getEmbargoDate()) + "."
                );
                break;
            case CLOSED:
                record.setAccessRights("The data in this project are not publicly available.");
                break;
        }
        if (StringUtils.isNotBlank(project.getRightsStatement())) {
            record.setRightsStatement(project.getRightsStatement());
        }
        List<Relation> relations = new ArrayList<Relation>();
        relations.add(
            new OaiPmhRecord.Relation(
                "isPartOf",
                configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier,
                configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier
            )
        );
        for (ProjectContribution contribution : project.getProjectContributions()) {
            String contributorLocalIdentifier = "people/" + contribution.getContributor().getId();
            relations.add(
                new OaiPmhRecord.Relation(
                    "hasContributor",
                    configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + contributorLocalIdentifier,
                    configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + contributorLocalIdentifier
                )
            );
        }
        relations.add(
            new OaiPmhRecord.Relation(
                "isManagedBy",
                configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.dataManagerPartyLocalIdentifier,
                configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.dataManagerPartyLocalIdentifier
            )
        );
        record.setRelations(relations);
        List<Subject> subjects = new ArrayList<Subject>(OaiPmhConstants.defaultRecordSubjects);
        if (StringUtils.isNotBlank(project.getSpeciesScientificName())) {
            subjects.add(new OaiPmhRecord.Subject("local", project.getSpeciesScientificName()));
        }
        record.setSubjects(subjects);
        record.setDcType("collection");
        record.setRifCsObjectElemName("collection");
        record.setRifCsObjectTypeAttr("dataset");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }
}
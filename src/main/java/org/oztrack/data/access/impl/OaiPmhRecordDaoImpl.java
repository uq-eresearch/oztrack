package org.oztrack.data.access.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.OaiPmhRecordProducer;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.data.model.types.OaiPmhRecord.Subject;
import org.oztrack.util.OaiPmhConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Polygon;

@Service
public class OaiPmhRecordDaoImpl implements OaiPmhRecordDao {
    private final SimpleDateFormat isoDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private ProjectDao projectDao;

    private OaiPmhRecord repositoryServiceRecord;
    private OaiPmhRecord oaiPmhServiceRecord;
    private OaiPmhRecord repositoryCollectionRecord;

    public OaiPmhRecordDaoImpl() {
    }

    @PostConstruct
    private void init() {
        this.repositoryServiceRecord = createRepositoryServiceRecord(configuration);
        this.oaiPmhServiceRecord = createOaiPmhServiceRecord(configuration);
        this.repositoryCollectionRecord = createRepositoryCollectionRecord(configuration);
    }

    @Override
    public OaiPmhRecordProducer getRecords() {
        return new OaiPmhChainingRecordProducer(Arrays.asList(
            createRepositoryRecordProducer(),
            createProjectRecordProducer()
        ));
    }

    private OaiPmhRecordProducer createRepositoryRecordProducer() {
        return new OaiPmhRecordProducer() {
            @Override
            public Iterator<OaiPmhRecord> iterator() {
                List<OaiPmhRecord> records = Arrays.asList(
                    repositoryServiceRecord,
                    oaiPmhServiceRecord,
                    repositoryCollectionRecord
                );
                return records.iterator();
            }
        };
    }

    private OaiPmhRecordProducer createProjectRecordProducer() {
        final List<Project> projects = projectDao.getAll();
        final HashMap<Long, Range<Date>> projectDetectionDateRanges = projectDao.getProjectDetectionDateRanges(false);
        final HashMap<Long, Polygon> projectBoundingBoxes = projectDao.getProjectBoundingBoxes(false);
        return new OaiPmhMappingRecordProducer<Project>(projects.iterator()) {
            @Override
            protected OaiPmhRecord map(Project project) {
                OaiPmhRecord record = new OaiPmhRecord();
                String localIdentifier = "projects/" + project.getId();
                record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + localIdentifier);
                record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + localIdentifier);
                record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + localIdentifier);
                record.setTitle(project.getTitle());
                record.setDescription(project.getDescription());
                record.setUrl(configuration.getBaseUrl() + "/projects/" + project.getId());
                record.setCreator(null);
                record.setCreateDate(project.getCreateDate());
                record.setUpdateDate(project.getUpdateDate());
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
                record.setRelations(Arrays.asList(
                    new OaiPmhRecord.Relation(
                        "isPartOf",
                        configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier,
                        configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier
                    )
                ));
                List<Subject> subjects = new ArrayList<Subject>(OaiPmhConstants.defaultRecordSubjects);
                if (StringUtils.isNotBlank(project.getSpeciesScientificName())) {
                    subjects.add(new OaiPmhRecord.Subject("local", project.getSpeciesScientificName()));
                }
                record.setSubjects(subjects);
                record.setDcType("collection");
                record.setRifCsObjectElemName("collection");
                record.setRifCsObjectTypeAttr("dataset");
                record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
                return record;
            }
        };
    }

    private static OaiPmhRecord createRepositoryServiceRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhConfiguration().getRepositoryServiceTitle());
        record.setDescription(configuration.getOaiPmhConfiguration().getRepositoryServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhConfiguration().getRepositoryServiceCreateDate());
        record.setUpdateDate(configuration.getOaiPmhConfiguration().getRepositoryServiceUpdateDate());
        record.setSubjects(OaiPmhConstants.defaultRecordSubjects);
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("report");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        return record;
    }

    private static OaiPmhRecord createOaiPmhServiceRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhConfiguration().getOaiPmhServiceTitle());
        record.setDescription(configuration.getOaiPmhConfiguration().getOaiPmhServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/oai-pmh");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhConfiguration().getOaiPmhServiceCreateDate());
        record.setUpdateDate(configuration.getOaiPmhConfiguration().getOaiPmhServiceUpdateDate());
        record.setRelations(Arrays.asList(
            new OaiPmhRecord.Relation(
                "isPartOf",
                configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier,
                configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier
            )
        ));
        record.setSubjects(OaiPmhConstants.defaultRecordSubjects);
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("harvest-oaipmh");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        return record;
    }

    private static OaiPmhRecord createRepositoryCollectionRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier);
        record.setTitle(configuration.getOaiPmhConfiguration().getRepositoryCollectionTitle());
        record.setDescription(configuration.getOaiPmhConfiguration().getRepositoryCollectionDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhConfiguration().getRepositoryCollectionCreateDate());
        record.setUpdateDate(configuration.getOaiPmhConfiguration().getRepositoryCollectionUpdateDate());
        record.setRelations(Arrays.asList(
            new OaiPmhRecord.Relation(
                "isPresentedBy",
                configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier,
                configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier
            ),
            new OaiPmhRecord.Relation(
                "isAvailableThrough",
                configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier,
                configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier
            )
        ));
        record.setSubjects(OaiPmhConstants.defaultRecordSubjects);
        record.setDcType("collection");
        record.setRifCsObjectElemName("collection");
        record.setRifCsObjectTypeAttr("repository");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        return record;
    }
}

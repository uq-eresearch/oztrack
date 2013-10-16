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
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.OaiPmhEntityProducer;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.ProjectContribution;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.data.model.types.OaiPmhRecord.Name.NamePart;
import org.oztrack.data.model.types.OaiPmhRecord.Relation;
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

    @Autowired
    private PersonDao personDao;

    @Autowired
    private InstitutionDao institutionDao;

    private OaiPmhRecord repositoryServiceRecord;
    private OaiPmhRecord oaiPmhServiceRecord;
    private OaiPmhRecord repositoryCollectionRecord;
    private OaiPmhRecord dataManagerPartyRecord;

    public OaiPmhRecordDaoImpl() {
    }

    @PostConstruct
    private void init() {
        this.repositoryServiceRecord = createRepositoryServiceRecord(configuration);
        this.oaiPmhServiceRecord = createOaiPmhServiceRecord(configuration);
        this.repositoryCollectionRecord = createRepositoryCollectionRecord(configuration);
        this.dataManagerPartyRecord = createDataManagerPartyRecord(configuration);
    }

    @Override
    public OaiPmhEntityProducer<OaiPmhRecord> getRecords() {
        @SuppressWarnings("unchecked")
        List<OaiPmhEntityProducer<OaiPmhRecord>> producers = Arrays.asList(
            createRepositoryRecordProducer(),
            createProjectRecordProducer(),
            createPersonRecordProducer(),
            createInstitutionRecordProducer()
        );
        return new OaiPmhChainingEntityProducer<OaiPmhRecord>(producers);
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createRepositoryRecordProducer() {
        return new OaiPmhEntityProducer<OaiPmhRecord>() {
            @Override
            public Iterator<OaiPmhRecord> iterator() {
                List<OaiPmhRecord> records = Arrays.asList(
                    repositoryServiceRecord,
                    oaiPmhServiceRecord,
                    repositoryCollectionRecord,
                    dataManagerPartyRecord
                );
                return records.iterator();
            }
        };
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createProjectRecordProducer() {
        final List<Project> projects = projectDao.getAll();
        final HashMap<Long, Range<Date>> projectDetectionDateRanges = projectDao.getProjectDetectionDateRanges(false);
        final HashMap<Long, Polygon> projectBoundingBoxes = projectDao.getProjectBoundingBoxes(false);
        return new OaiPmhMappingEntityProducer<Project, OaiPmhRecord>(projects.iterator()) {
            @Override
            protected OaiPmhRecord map(Project project) {
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
        };
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createPersonRecordProducer() {
        final List<Person> people = personDao.getAll();
        return new OaiPmhMappingEntityProducer<Person, OaiPmhRecord>(people.iterator()) {
            @Override
            protected OaiPmhRecord map(Person person) {
                OaiPmhRecord record = new OaiPmhRecord();
                String localIdentifier = "people/" + person.getId();
                record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + localIdentifier);
                record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + localIdentifier);
                record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + localIdentifier);
                if (StringUtils.isNotBlank(person.getEmail())) {
                    record.setUriIdentifiers(Arrays.asList("mailto:" + person.getEmail()));
                    record.setEmail(person.getEmail());
                }
                List<NamePart> nameParts = new ArrayList<NamePart>();
                if (StringUtils.isNotBlank(person.getTitle())) {
                    nameParts.add(new OaiPmhRecord.Name.NamePart("title", person.getTitle()));
                }
                if (StringUtils.isNotBlank(person.getFirstName())) {
                    nameParts.add(new OaiPmhRecord.Name.NamePart("given", person.getFirstName()));
                }
                if (StringUtils.isNotBlank(person.getLastName())) {
                    nameParts.add(new OaiPmhRecord.Name.NamePart("family", person.getLastName()));
                }
                if (!nameParts.isEmpty()) {
                    record.setName(new OaiPmhRecord.Name(nameParts));
                }
                record.setDescription(person.getDescription());
                record.setCreator(null);
                record.setRecordCreateDate(person.getCreateDate());
                record.setRecordUpdateDate(person.getUpdateDate());
                List<Relation> relations = new ArrayList<Relation>();
                for (Institution institution : person.getInstitutions()) {
                    String institutionLocalIdentifier = "institutions/" + institution.getId();
                    relations.add(
                        new OaiPmhRecord.Relation(
                            "isMemberOf",
                            configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + institutionLocalIdentifier,
                            configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + institutionLocalIdentifier
                        )
                    );
                }
                for (ProjectContribution contribution : person.getProjectContributions()) {
                    String projectLocalIdentifier = "project/" + contribution.getProject().getId();
                    relations.add(
                        new OaiPmhRecord.Relation(
                            "isCollectorOf",
                            configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + projectLocalIdentifier,
                            configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + projectLocalIdentifier
                        )
                    );
                }
                record.setRelations(relations);
                record.setSubjects(new ArrayList<Subject>(OaiPmhConstants.defaultRecordSubjects));
                record.setDcType("agent");
                record.setRifCsObjectElemName("party");
                record.setRifCsObjectTypeAttr("person");
                record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
                record.setOriginatingSource(configuration.getBaseUrl() + "/");
                return record;
            }
        };
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createInstitutionRecordProducer() {
        final List<Institution> institutions = institutionDao.getAll();
        return new OaiPmhMappingEntityProducer<Institution, OaiPmhRecord>(institutions.iterator()) {
            @Override
            protected OaiPmhRecord map(Institution institution) {
                OaiPmhRecord record = new OaiPmhRecord();
                String localIdentifier = "institutions/" + institution.getId();
                record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + localIdentifier);
                record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + localIdentifier);
                record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + localIdentifier);
                if (StringUtils.isNotBlank(institution.getDomainName())) {
                    String domainNameUrl = "http://" + institution.getDomainName() + "/";
                    record.setUriIdentifiers(Arrays.asList(domainNameUrl));
                    record.setUrl(domainNameUrl);
                }
                record.setName(new OaiPmhRecord.Name(Arrays.asList(new OaiPmhRecord.Name.NamePart(null, institution.getTitle()))));
                record.setCreator(null);
                record.setRecordCreateDate(institution.getCreateDate());
                record.setRecordUpdateDate(institution.getUpdateDate());
                record.setSubjects(new ArrayList<Subject>(OaiPmhConstants.defaultRecordSubjects));
                record.setDcType("agent");
                record.setRifCsObjectElemName("party");
                record.setRifCsObjectTypeAttr("group");
                record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
                record.setOriginatingSource(configuration.getBaseUrl() + "/");
                return record;
            }
        };
    }

    private static OaiPmhRecord createRepositoryServiceRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryServiceLocalIdentifier);
        String title = configuration.getOaiPmhConfiguration().getRepositoryServiceTitle();
        record.setName(new OaiPmhRecord.Name(Arrays.asList(new OaiPmhRecord.Name.NamePart(null, title))));
        record.setDescription(configuration.getOaiPmhConfiguration().getRepositoryServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setRecordCreateDate(configuration.getOaiPmhConfiguration().getRepositoryServiceCreateDate());
        record.setRecordUpdateDate(configuration.getOaiPmhConfiguration().getRepositoryServiceUpdateDate());
        record.setExistenceStartDate(configuration.getOaiPmhConfiguration().getRepositoryServiceCreateDate());
        record.setSubjects(OaiPmhConstants.defaultRecordSubjects);
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("report");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }

    private static OaiPmhRecord createOaiPmhServiceRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.oaiPmhServiceLocalIdentifier);
        String title = configuration.getOaiPmhConfiguration().getOaiPmhServiceTitle();
        record.setName(new OaiPmhRecord.Name(Arrays.asList(new OaiPmhRecord.Name.NamePart(null, title))));
        record.setDescription(configuration.getOaiPmhConfiguration().getOaiPmhServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/oai-pmh");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setRecordCreateDate(configuration.getOaiPmhConfiguration().getOaiPmhServiceCreateDate());
        record.setRecordUpdateDate(configuration.getOaiPmhConfiguration().getOaiPmhServiceUpdateDate());
        record.setExistenceStartDate(configuration.getOaiPmhConfiguration().getOaiPmhServiceCreateDate());
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
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }

    private static OaiPmhRecord createRepositoryCollectionRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier);
        String title = configuration.getOaiPmhConfiguration().getRepositoryCollectionTitle();
        record.setName(new OaiPmhRecord.Name(Arrays.asList(new OaiPmhRecord.Name.NamePart(null, title))));
        record.setDescription(configuration.getOaiPmhConfiguration().getRepositoryCollectionDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setRecordCreateDate(configuration.getOaiPmhConfiguration().getRepositoryCollectionCreateDate());
        record.setRecordUpdateDate(configuration.getOaiPmhConfiguration().getRepositoryCollectionUpdateDate());
        record.setExistenceStartDate(configuration.getOaiPmhConfiguration().getRepositoryCollectionCreateDate());
        record.setRightsStatement(configuration.getOaiPmhConfiguration().getRepositoryCollectionRightsStatement());
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
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }

    private static OaiPmhRecord createDataManagerPartyRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhRecordIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix() + OaiPmhConstants.dataManagerPartyLocalIdentifier);
        record.setRifCsRecordIdentifier(configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.dataManagerPartyLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.dataManagerPartyLocalIdentifier);
        String title = configuration.getOaiPmhConfiguration().getDataManagerPartyName();
        record.setName(new OaiPmhRecord.Name(Arrays.asList(new OaiPmhRecord.Name.NamePart(null, title))));
        record.setDescription(configuration.getOaiPmhConfiguration().getDataManagerPartyDescription());
        record.setEmail(configuration.getOaiPmhConfiguration().getDataManagerPartyEmail());
        record.setRecordCreateDate(configuration.getOaiPmhConfiguration().getDataManagerPartyCreateDate());
        record.setRecordUpdateDate(configuration.getOaiPmhConfiguration().getDataManagerPartyUpdateDate());
        record.setExistenceStartDate(configuration.getOaiPmhConfiguration().getDataManagerPartyCreateDate());
        record.setRelations(Arrays.asList(
            new OaiPmhRecord.Relation(
                "isManagerOf",
                configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier,
                configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier
            )
        ));
        record.setSubjects(OaiPmhConstants.defaultRecordSubjects);
        record.setDcType("agent");
        record.setRifCsObjectElemName("party");
        record.setRifCsObjectTypeAttr("administrativePosition");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }
}

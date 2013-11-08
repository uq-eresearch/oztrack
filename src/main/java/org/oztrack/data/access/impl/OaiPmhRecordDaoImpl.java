package org.oztrack.data.access.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Range;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.OaiPmhEntityProducer;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.util.OaiPmhConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vividsolutions.jts.geom.Polygon;

@Service
public class OaiPmhRecordDaoImpl implements OaiPmhRecordDao {
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
    public OaiPmhRecord getRecordByOaiPmhRecordIdentifier(String identifier) {
        String oaiPmhRecordIdentifierPrefix = configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix();
        String localIdentifier = identifier.substring(oaiPmhRecordIdentifierPrefix.length());
        if (localIdentifier.equals("service")) {
            return repositoryServiceRecord;
        }
        if (localIdentifier.equals("oai-pmh")) {
            return oaiPmhServiceRecord;
        }
        if (localIdentifier.equals("collection")) {
            return repositoryCollectionRecord;
        }
        if (localIdentifier.equals("data-manager")) {
            return dataManagerPartyRecord;
        }
        Matcher matcher = Pattern.compile("^([a-z-]+)/([0-9]+)$").matcher(localIdentifier);
        if (!matcher.matches()) {
            return null;
        }
        String recordType = matcher.group(1);
        Long recordId = Long.valueOf(matcher.group(2));
        if (recordType.equals("projects")) {
            Project project = projectDao.getProjectById(recordId);
            final Map<Long, Range<Date>> projectDetectionDateRanges = new HashMap<Long, Range<Date>>();
            projectDetectionDateRanges.put(project.getId(), projectDao.getDetectionDateRange(project, false));
            final Map<Long, Polygon> projectBoundingBoxes = new HashMap<Long, Polygon>();
            projectBoundingBoxes.put(project.getId(), projectDao.getBoundingBox(project, false));
            OaiPmhProjectRecordMapper mapper = new OaiPmhProjectRecordMapper(configuration, projectDetectionDateRanges, projectBoundingBoxes);
            return mapper.map(project);
        }
        if (recordType.equals("people")) {
            Person person = personDao.getById(recordId);
            OaiPmhPersonRecordMapper mapper = new OaiPmhPersonRecordMapper(configuration);
            return mapper.map(person);
        }
        if (recordType.equals("institutions")) {
            Institution institution = institutionDao.getById(recordId);
            OaiPmhInstitutionRecordMapper mapper = new OaiPmhInstitutionRecordMapper(configuration);
            return mapper.map(institution);
        }
        return null;
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
        OaiPmhProjectRecordMapper mapper = new OaiPmhProjectRecordMapper(configuration, projectDetectionDateRanges, projectBoundingBoxes);
        return new OaiPmhMappingEntityProducer<Project, OaiPmhRecord>(projects.iterator(), mapper);
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createPersonRecordProducer() {
        final List<Person> people = personDao.getAll();
        OaiPmhPersonRecordMapper mapper = new OaiPmhPersonRecordMapper(configuration);
        return new OaiPmhMappingEntityProducer<Person, OaiPmhRecord>(people.iterator(), mapper);
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createInstitutionRecordProducer() {
        final List<Institution> institutions = institutionDao.getAll();
        OaiPmhInstitutionRecordMapper mapper = new OaiPmhInstitutionRecordMapper(configuration);
        return new OaiPmhMappingEntityProducer<Institution, OaiPmhRecord>(institutions.iterator(), mapper);
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

package org.oztrack.data.access.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.Range;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.InstitutionDao;
import org.oztrack.data.access.OaiPmhEntityProducer;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.OaiPmhSetDao;
import org.oztrack.data.access.PersonDao;
import org.oztrack.data.access.ProjectDao;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.Project;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.data.model.types.OaiPmhSet;
import org.oztrack.util.OaiPmhConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Polygon;

@Service
public class OaiPmhRecordDaoImpl implements OaiPmhRecordDao {
    private EntityManager em;

    @PersistenceContext
    public void setEntityManger(EntityManager em) {
        this.em = em;
    }

    @Autowired
    private OzTrackConfiguration configuration;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private PersonDao personDao;

    @Autowired
    private InstitutionDao institutionDao;

    @Autowired
    private OaiPmhSetDao oaiPmhSetDao;

    public OaiPmhRecordDaoImpl() {
    }

    @Override
    public OaiPmhRecord getRecordByOaiPmhRecordIdentifier(String identifier) {
        String oaiPmhRecordIdentifierPrefix = configuration.getOaiPmhConfiguration().getOaiPmhRecordIdentifierPrefix();
        String localIdentifier = identifier.substring(oaiPmhRecordIdentifierPrefix.length());
        if (localIdentifier.equals("service")) {
            return createRepositoryServiceRecord();
        }
        if (localIdentifier.equals("oai-pmh")) {
            return createOaiPmhServiceRecord();
        }
        if (localIdentifier.equals("collection")) {
            return createRepositoryCollectionRecord();
        }
        if (localIdentifier.equals("data-manager")) {
            return createDataManagerPartyRecord();
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
    public OaiPmhEntityProducer<OaiPmhRecord> getRecords(Date from, Date until, String setSpec) {
        @SuppressWarnings("unchecked")
        List<OaiPmhEntityProducer<OaiPmhRecord>> producers = Arrays.asList(
            createRepositoryRecordProducer(from, until, setSpec),
            createProjectRecordProducer(from, until, setSpec),
            createPersonRecordProducer(from, until, setSpec),
            createInstitutionRecordProducer(from, until, setSpec)
        );
        return new OaiPmhChainingEntityProducer<OaiPmhRecord>(producers);
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createRepositoryRecordProducer(Date from, Date until, final String setSpec) {
        final List<OaiPmhRecord> unfilteredRecords = Arrays.asList(
            createRepositoryServiceRecord(),
            createOaiPmhServiceRecord(),
            createRepositoryCollectionRecord(),
            createDataManagerPartyRecord()
        );
        final List<OaiPmhRecord> filteredRecords = new ArrayList<OaiPmhRecord>();
        for (OaiPmhRecord record : unfilteredRecords) {
            Date datestamp = record.getRecordDatestampDate();
            if (
                ((from == null) || !datestamp.before(from)) &&
                ((until == null) || !datestamp.after(until)) &&
                ((setSpec == null) || CollectionUtils.exists(record.getOaiPmhSetSpecs(), new Predicate() {
                    @Override
                    public boolean evaluate(Object object) {
                        String recordSetSpec = (String) object;
                        return recordSetSpec.matches("^" + setSpec + "(:.*)?$");
                    }
                }))
            ) {
                filteredRecords.add(record);
            }
        }
        return new OaiPmhEntityProducer<OaiPmhRecord>() {
            @Override
            public Iterator<OaiPmhRecord> iterator() {
                return filteredRecords.iterator();
            }
        };
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createProjectRecordProducer(Date from, Date until, String setSpec) {
        final List<Project> projects = projectDao.getProjectsForOaiPmh(from, until, setSpec);
        final HashMap<Long, Range<Date>> projectDetectionDateRanges = projectDao.getProjectDetectionDateRanges(false);
        final HashMap<Long, Polygon> projectBoundingBoxes = projectDao.getProjectBoundingBoxes(false);
        OaiPmhProjectRecordMapper mapper = new OaiPmhProjectRecordMapper(configuration, projectDetectionDateRanges, projectBoundingBoxes);
        return new OaiPmhMappingEntityProducer<Project, OaiPmhRecord>(projects.iterator(), mapper);
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createPersonRecordProducer(Date from, Date until, String setSpec) {
        final List<Person> people = personDao.getPeopleForOaiPmh(from, until, setSpec);
        OaiPmhPersonRecordMapper mapper = new OaiPmhPersonRecordMapper(configuration);
        return new OaiPmhMappingEntityProducer<Person, OaiPmhRecord>(people.iterator(), mapper);
    }

    private OaiPmhEntityProducer<OaiPmhRecord> createInstitutionRecordProducer(Date from, Date until, String setSpec) {
        final List<Institution> institutions = institutionDao.getInstitutionsForOaiPmh(from, until, setSpec);
        OaiPmhInstitutionRecordMapper mapper = new OaiPmhInstitutionRecordMapper(configuration);
        return new OaiPmhMappingEntityProducer<Institution, OaiPmhRecord>(institutions.iterator(), mapper);
    }

    private OaiPmhRecord createRepositoryServiceRecord() {
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
        record.setOaiPmhSetSpecs(getAllSetSpecs());
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("report");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }

    private OaiPmhRecord createOaiPmhServiceRecord() {
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
        record.setOaiPmhSetSpecs(getAllSetSpecs());
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("harvest-oaipmh");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }

    private OaiPmhRecord createRepositoryCollectionRecord() {
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
        record.setOaiPmhSetSpecs(getAllSetSpecs());
        record.setDcType("collection");
        record.setRifCsObjectElemName("collection");
        record.setRifCsObjectTypeAttr("repository");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }

    private OaiPmhRecord createDataManagerPartyRecord() {
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
        record.setOaiPmhSetSpecs(getAllSetSpecs());
        record.setDcType("agent");
        record.setRifCsObjectElemName("party");
        record.setRifCsObjectTypeAttr("administrativePosition");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }

    private SortedSet<String> getAllSetSpecs() {
        SortedSet<String> setSpecs = new TreeSet<String>();
        OaiPmhEntityProducer<OaiPmhSet> sets = oaiPmhSetDao.getSets();
        for (OaiPmhSet set : sets) {
            setSpecs.add(set.getSetSpec());
        }
        return setSpecs;
    }

    @Override
    @Transactional
    public void updateOaiPmhSets() {
        em.createNativeQuery(
            "create temp table all_oaipmhset on commit drop as\n" +
            "select\n" +
            "    project.id as project_id,\n" +
            "    person.id as person_id,\n" +
            "    null as institution_id,\n" +
            "    'country:' || lower(country.code) as oaipmhset\n" +
            "from\n" +
            "    project\n" +
            "    inner join project_contribution on project.id = project_contribution.project_id\n" +
            "    inner join person on person.id = project_contribution.contributor_id\n" +
            "    inner join country on person.country_id = country.id\n" +
            "union select\n" +
            "    project.id as project_id,\n" +
            "    person.id as person_id,\n" +
            "    institution.id as institution_id,\n" +
            "    'country:' || lower(country.code) as oaipmhset\n" +
            "from\n" +
            "    project\n" +
            "    inner join project_contribution on project.id = project_contribution.project_id\n" +
            "    inner join person on person.id = project_contribution.contributor_id\n" +
            "    inner join person_institution on person.id = person_institution.person_id\n" +
            "    inner join institution on institution.id = person_institution.institution_id\n" +
            "    inner join country on institution.country_id = country.id\n" +
            "union select\n" +
            "    project.id as project_id,\n" +
            "    person.id as person_id,\n" +
            "    person_institution.institution_id as institution_id,\n" +
            "    'institution:' || person_institution.institution_id as oaipmhset\n" +
            "from\n" +
            "    project\n" +
            "    inner join project_contribution on project.id = project_contribution.project_id\n" +
            "    inner join person on person.id = project_contribution.contributor_id\n" +
            "    inner join person_institution on person.id = person_institution.person_id;\n" +
            "\n" +
            "create temp table new_project_oaipmhset on commit drop as\n" +
            "select distinct project_id, oaipmhset from all_oaipmhset\n" +
            "except select project_id, oaipmhset from project_oaipmhset;\n" +
            "\n" +
            "create temp table new_person_oaipmhset on commit drop as\n" +
            "select distinct a.person_id, b.oaipmhset\n" +
            "from all_oaipmhset a, all_oaipmhset b\n" +
            "where a.project_id = b.project_id\n" +
            "except select person_id, oaipmhset from person_oaipmhset;\n" +
            "\n" +
            "create temp table new_institution_oaipmhset on commit drop as\n" +
            "select distinct a.institution_id, b.oaipmhset\n" +
            "from all_oaipmhset a, all_oaipmhset b\n" +
            "where a.project_id = b.project_id\n" +
            "except select institution_id, oaipmhset from institution_oaipmhset;\n" +
            "\n" +
            "insert into project_oaipmhset select * from new_project_oaipmhset;\n" +
            "insert into person_oaipmhset select * from new_person_oaipmhset;\n" +
            "insert into institution_oaipmhset select * from new_institution_oaipmhset;\n" +
            "\n" +
            "update project set updatedateforoaipmh = now()\n" +
            "where id in (select project_id from new_project_oaipmhset);\n" +
            "update person set updatedateforoaipmh = now()\n" +
            "where id in (select person_id from new_person_oaipmhset);\n" +
            "update institution set updatedateforoaipmh = now()\n" +
            "where id in (select institution_id from new_institution_oaipmhset);"
        );
    }
}

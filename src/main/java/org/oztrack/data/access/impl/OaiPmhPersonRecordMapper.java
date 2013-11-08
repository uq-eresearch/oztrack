package org.oztrack.data.access.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.OaiPmhEntityMapper;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.Person;
import org.oztrack.data.model.ProjectContribution;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.data.model.types.OaiPmhRecord.Name.NamePart;
import org.oztrack.data.model.types.OaiPmhRecord.Relation;
import org.oztrack.data.model.types.OaiPmhRecord.Subject;
import org.oztrack.util.OaiPmhConstants;

public class OaiPmhPersonRecordMapper implements OaiPmhEntityMapper<Person, OaiPmhRecord> {
    private final OzTrackConfiguration configuration;

    public OaiPmhPersonRecordMapper(OzTrackConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public OaiPmhRecord map(Person person) {
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
        relations.add(new OaiPmhRecord.Relation(
            "hasAssociationWith",
            configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier,
            configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier
        ));
        record.setRelations(relations);
        record.setSubjects(new ArrayList<Subject>(OaiPmhConstants.defaultRecordSubjects));
        record.setDcType("agent");
        record.setRifCsObjectElemName("party");
        record.setRifCsObjectTypeAttr("person");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }
}
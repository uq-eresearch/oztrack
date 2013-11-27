package org.oztrack.data.access.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.OaiPmhEntityMapper;
import org.oztrack.data.model.Institution;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.data.model.types.OaiPmhRecord.Subject;
import org.oztrack.util.OaiPmhConstants;

public class OaiPmhInstitutionRecordMapper implements OaiPmhEntityMapper<Institution, OaiPmhRecord> {
    private final OzTrackConfiguration configuration;

    public OaiPmhInstitutionRecordMapper(OzTrackConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public OaiPmhRecord map(Institution institution) {
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
        record.setRelations(Arrays.asList(
            new OaiPmhRecord.Relation(
                "hasAssociationWith",
                configuration.getOaiPmhConfiguration().getRifCsRecordIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier,
                configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + OaiPmhConstants.repositoryCollectionLocalIdentifier
            )
        ));
        record.setSubjects(new ArrayList<Subject>(OaiPmhConstants.defaultRecordSubjects));
        record.setOaiPmhSetSpecs(institution.getOaiPmhSetSpecs());
        record.setDcType("agent");
        record.setRifCsObjectElemName("party");
        record.setRifCsObjectTypeAttr("group");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        record.setOriginatingSource(configuration.getBaseUrl() + "/");
        return record;
    }
}
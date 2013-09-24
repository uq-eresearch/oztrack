package org.oztrack.data.access.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.oztrack.app.OzTrackConfiguration;
import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.OaiPmhRecordProducer;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OaiPmhRecordDaoImpl implements OaiPmhRecordDao {
    private static final String repositoryServiceLocalIdentifier = "service";
    private static final String oaiPmhServiceLocalIdentifier = "oai-pmh";
    private static final String repositoryCollectionLocalIdentifier = "collection";

    @Autowired
    private OzTrackConfiguration configuration;

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

    private static OaiPmhRecord createRepositoryServiceRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(configuration.getOaiPmhOaiPmhIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhObjectIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhRepositoryServiceTitle());
        record.setDescription(configuration.getOaiPmhRepositoryServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhRepositoryServiceCreateDate());
        record.setUpdateDate(configuration.getOaiPmhRepositoryServiceUpdateDate());
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("report");
        record.setRifCsGroup(configuration.getOaiPmhRifCsGroup());
        return record;
    }

    private static OaiPmhRecord createOaiPmhServiceRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(configuration.getOaiPmhOaiPmhIdentifierPrefix() + oaiPmhServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhObjectIdentifierPrefix() + oaiPmhServiceLocalIdentifier);
        record.setIsPartOfObjectIdentifier(configuration.getOaiPmhObjectIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhOaiPmhServiceTitle());
        record.setDescription(configuration.getOaiPmhOaiPmhServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/oai-pmh");
        record.setCreator(configuration.getOaiPmhRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhOaiPmhServiceCreateDate());
        record.setUpdateDate(configuration.getOaiPmhOaiPmhServiceUpdateDate());
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("harvest-oaipmh");
        record.setRifCsGroup(configuration.getOaiPmhRifCsGroup());
        return record;
    }

    private static OaiPmhRecord createRepositoryCollectionRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(configuration.getOaiPmhOaiPmhIdentifierPrefix() + repositoryCollectionLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhObjectIdentifierPrefix() + repositoryCollectionLocalIdentifier);
        record.setIsPresentedByObjectIdentifier(configuration.getOaiPmhObjectIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setIsAvailableThroughObjectIdentifier(configuration.getOaiPmhObjectIdentifierPrefix() + oaiPmhServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhRepositoryCollectionTitle());
        record.setDescription(configuration.getOaiPmhRepositoryCollectionDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhRepositoryCollectionCreateDate());
        record.setUpdateDate(configuration.getOaiPmhRepositoryCollectionUpdateDate());
        record.setDcType("collection");
        record.setRifCsObjectElemName("collection");
        record.setRifCsObjectTypeAttr("repository");
        record.setRifCsGroup(configuration.getOaiPmhRifCsGroup());
        return record;
    }
}

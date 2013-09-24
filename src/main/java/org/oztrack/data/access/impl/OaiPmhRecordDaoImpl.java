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
        record.setOaiPmhIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhConfiguration().getRepositoryServiceTitle());
        record.setDescription(configuration.getOaiPmhConfiguration().getRepositoryServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhConfiguration().getRepositoryServiceCreateDate());
        record.setUpdateDate(configuration.getOaiPmhConfiguration().getRepositoryServiceUpdateDate());
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("report");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        return record;
    }

    private static OaiPmhRecord createOaiPmhServiceRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhIdentifierPrefix() + oaiPmhServiceLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + oaiPmhServiceLocalIdentifier);
        record.setIsPartOfObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhConfiguration().getOaiPmhServiceTitle());
        record.setDescription(configuration.getOaiPmhConfiguration().getOaiPmhServiceDescription());
        record.setUrl(configuration.getBaseUrl() + "/oai-pmh");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhConfiguration().getOaiPmhServiceCreateDate());
        record.setUpdateDate(configuration.getOaiPmhConfiguration().getOaiPmhServiceUpdateDate());
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("harvest-oaipmh");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        return record;
    }

    private static OaiPmhRecord createRepositoryCollectionRecord(OzTrackConfiguration configuration) {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(configuration.getOaiPmhConfiguration().getOaiPmhIdentifierPrefix() + repositoryCollectionLocalIdentifier);
        record.setObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + repositoryCollectionLocalIdentifier);
        record.setIsPresentedByObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + repositoryServiceLocalIdentifier);
        record.setIsAvailableThroughObjectIdentifier(configuration.getOaiPmhConfiguration().getObjectIdentifierPrefix() + oaiPmhServiceLocalIdentifier);
        record.setTitle(configuration.getOaiPmhConfiguration().getRepositoryCollectionTitle());
        record.setDescription(configuration.getOaiPmhConfiguration().getRepositoryCollectionDescription());
        record.setUrl(configuration.getBaseUrl() + "/");
        record.setCreator(configuration.getOaiPmhConfiguration().getRepositoryCreator());
        record.setCreateDate(configuration.getOaiPmhConfiguration().getRepositoryCollectionCreateDate());
        record.setUpdateDate(configuration.getOaiPmhConfiguration().getRepositoryCollectionUpdateDate());
        record.setDcType("collection");
        record.setRifCsObjectElemName("collection");
        record.setRifCsObjectTypeAttr("repository");
        record.setRifCsGroup(configuration.getOaiPmhConfiguration().getRifCsGroup());
        return record;
    }
}

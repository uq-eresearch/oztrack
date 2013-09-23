package org.oztrack.data.access.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.oztrack.data.access.OaiPmhRecordProducer;
import org.oztrack.data.model.types.OaiPmhRecord;

public class OaiPmhRepositoryRecordProducer implements OaiPmhRecordProducer {
    private SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final String oaiPmhIdentifierPrefix;
    private final String objectIdentifierPrefix;
    private final String rifCsGroup;

    private final String repositoryServiceLocalIdentifier;
    private final String oaiPmhServiceLocalIdentifier;
    private final String repositoryCollectionLocalIdentifier;
    private final Date repositoryCreateDate;
    private final Date repositoryUpdateDate;

    public OaiPmhRepositoryRecordProducer() {
        this.oaiPmhIdentifierPrefix = "oai:oztrack.org:";
        this.objectIdentifierPrefix = "http://oztrack.org/id/";
        this.rifCsGroup = "OzTrack";

        this.repositoryServiceLocalIdentifier = "service";
        this.oaiPmhServiceLocalIdentifier = "oai-pmh";
        this.repositoryCollectionLocalIdentifier = "collection";
        try {
            this.repositoryCreateDate = utcDateTimeFormat.parse("2011-11-02T03:47:24Z");
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.repositoryUpdateDate = this.repositoryCreateDate;
    }

    @Override
    public Iterator<OaiPmhRecord> iterator() {
        List<OaiPmhRecord> records = Arrays.asList(
            createRepositoryServiceRecord(),
            createOaiPmhServiceRecord(),
            createRepositoryCollectionRecord()
        );
        return records.iterator();
    }

    private OaiPmhRecord createRepositoryServiceRecord() {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(oaiPmhIdentifierPrefix + repositoryServiceLocalIdentifier);
        record.setObjectIdentifier(objectIdentifierPrefix + repositoryServiceLocalIdentifier);
        record.setTitle("OzTrack");
        record.setDescription(
            "OzTrack is a free-to-use web-based platform for analysing and " +
            "visualising individual-based animal location data."
        );
        record.setUrl("http://oztrack.org/");
        record.setCreator("The University of Queensland");
        record.setCreateDate(repositoryCreateDate);
        record.setUpdateDate(repositoryUpdateDate);
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("report");
        record.setRifCsGroup(rifCsGroup);
        return record;
    }

    private OaiPmhRecord createOaiPmhServiceRecord() {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(oaiPmhIdentifierPrefix + oaiPmhServiceLocalIdentifier);
        record.setObjectIdentifier(objectIdentifierPrefix + oaiPmhServiceLocalIdentifier);
        record.setIsPartOfObjectIdentifier(objectIdentifierPrefix + repositoryServiceLocalIdentifier);
        record.setTitle("OzTrack OAI-PMH Feed");
        record.setDescription(
            "OzTrack is a free-to-use web-based platform for analysing and " +
            "visualising individual-based animal location data. " +
            "This feed allows records to be harvested using the OAI-PMH protocol."
        );
        record.setUrl("http://oztrack.org/oai-pmh");
        record.setCreator("The University of Queensland");
        record.setCreateDate(repositoryCreateDate);
        record.setUpdateDate(repositoryUpdateDate);
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("harvest-oaipmh");
        record.setRifCsGroup(rifCsGroup);
        return record;
    }

    private OaiPmhRecord createRepositoryCollectionRecord() {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier(oaiPmhIdentifierPrefix + repositoryCollectionLocalIdentifier);
        record.setObjectIdentifier(objectIdentifierPrefix + repositoryCollectionLocalIdentifier);
        record.setIsPresentedByObjectIdentifier(objectIdentifierPrefix + repositoryServiceLocalIdentifier);
        record.setIsAvailableThroughObjectIdentifier(objectIdentifierPrefix + oaiPmhServiceLocalIdentifier);
        record.setTitle("OzTrack Data Collection");
        record.setDescription(
            "OzTrack is a free-to-use web-based platform for analysing and " +
            "visualising individual-based animal location data. " +
            "This data collection contains animal tracking projects uploaded " +
            "by users of OzTrack."
        );
        record.setUrl("http://oztrack.org/");
        record.setCreator("The University of Queensland");
        record.setCreateDate(repositoryCreateDate);
        record.setUpdateDate(repositoryUpdateDate);
        record.setDcType("collection");
        record.setRifCsObjectElemName("collection");
        record.setRifCsObjectTypeAttr("repository");
        record.setRifCsGroup(rifCsGroup);
        return record;
    }
}

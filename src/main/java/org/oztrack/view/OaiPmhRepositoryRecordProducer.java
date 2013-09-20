package org.oztrack.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class OaiPmhRepositoryRecordProducer implements OaiPmhRecordProducer {
    private SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final String repositoryIdentifier;
    private final Date oztrackCreateDate;
    private final Date oztrackUpdateDate;
    private final String rifCsGroup;

    public OaiPmhRepositoryRecordProducer() {
        this.repositoryIdentifier = "http://oztrack.org/id/repository";
        try {
            this.oztrackCreateDate = utcDateTimeFormat.parse("2011-11-02T03:47:24Z");
        }
        catch (ParseException e) {
            throw new RuntimeException(e);
        }
        this.oztrackUpdateDate = this.oztrackCreateDate;
        this.rifCsGroup = "OzTrack";
    }

    @Override
    public Iterator<OaiPmhRecord> iterator() {
        List<OaiPmhRecord> records = Arrays.asList(
            createRepositoryRecord(),
            createOaiPmhFeedRecord()
        );
        return records.iterator();
    }

    private OaiPmhRecord createRepositoryRecord() {
        OaiPmhRecord repositoryRecord = new OaiPmhRecord();
        repositoryRecord.setIdentifier(repositoryIdentifier);
        repositoryRecord.setTitle(rifCsGroup);
        repositoryRecord.setDescription(
            "OzTrack is a free-to-use web-based platform for analysing and " +
            "visualising individual-based animal location data."
        );
        repositoryRecord.setUrl("http://oztrack.org/");
        repositoryRecord.setCreator("The University of Queensland");
        repositoryRecord.setCreateDate(oztrackCreateDate);
        repositoryRecord.setUpdateDate(oztrackUpdateDate);
        repositoryRecord.setDcType("Service");
        repositoryRecord.setRifCsObjectElemName("service");
        repositoryRecord.setRifCsObjectTypeAttr("report");
        repositoryRecord.setRifCsGroup(rifCsGroup);
        return repositoryRecord;
    }

    private OaiPmhRecord createOaiPmhFeedRecord() {
        OaiPmhRecord repositoryRecord = new OaiPmhRecord();
        repositoryRecord.setIdentifier("http://oztrack.org/id/oai-pmh");
        repositoryRecord.setParentObjectIdentifier(repositoryIdentifier);
        repositoryRecord.setTitle(rifCsGroup);
        repositoryRecord.setDescription("OzTrack OAI-PMH feed.");
        repositoryRecord.setUrl("http://oztrack.org/oai-pmh");
        repositoryRecord.setCreator("The University of Queensland");
        repositoryRecord.setCreateDate(oztrackCreateDate);
        repositoryRecord.setUpdateDate(oztrackUpdateDate);
        repositoryRecord.setDcType("Service");
        repositoryRecord.setRifCsObjectElemName("service");
        repositoryRecord.setRifCsObjectTypeAttr("harvest-oaipmh");
        repositoryRecord.setRifCsGroup(rifCsGroup);
        return repositoryRecord;
    }
}

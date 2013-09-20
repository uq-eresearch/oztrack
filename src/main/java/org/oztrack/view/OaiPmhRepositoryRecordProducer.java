package org.oztrack.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class OaiPmhRepositoryRecordProducer implements OaiPmhRecordProducer {
    private SimpleDateFormat utcDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private final String repositoryServiceObjectIdentifier;
    private final Date oztrackCreateDate;
    private final Date oztrackUpdateDate;
    private final String rifCsGroup;

    public OaiPmhRepositoryRecordProducer() {
        this.repositoryServiceObjectIdentifier = "http://oztrack.org/id/service";
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
            createRepositoryServiceRecord(),
            createOaiPmhServiceRecord()
        );
        return records.iterator();
    }

    private OaiPmhRecord createRepositoryServiceRecord() {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier("oai:oztrack.org:service");
        record.setObjectIdentifier(repositoryServiceObjectIdentifier);
        record.setTitle(rifCsGroup);
        record.setDescription(
            "OzTrack is a free-to-use web-based platform for analysing and " +
            "visualising individual-based animal location data."
        );
        record.setUrl("http://oztrack.org/");
        record.setCreator("The University of Queensland");
        record.setCreateDate(oztrackCreateDate);
        record.setUpdateDate(oztrackUpdateDate);
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("report");
        record.setRifCsGroup(rifCsGroup);
        return record;
    }

    private OaiPmhRecord createOaiPmhServiceRecord() {
        OaiPmhRecord record = new OaiPmhRecord();
        record.setOaiPmhIdentifier("oai:oztrack.org:oai-pmh");
        record.setObjectIdentifier("http://oztrack.org/id/oai-pmh");
        record.setParentObjectIdentifier(repositoryServiceObjectIdentifier);
        record.setTitle(rifCsGroup);
        record.setDescription("OzTrack OAI-PMH feed.");
        record.setUrl("http://oztrack.org/oai-pmh");
        record.setCreator("The University of Queensland");
        record.setCreateDate(oztrackCreateDate);
        record.setUpdateDate(oztrackUpdateDate);
        record.setDcType("service");
        record.setRifCsObjectElemName("service");
        record.setRifCsObjectTypeAttr("harvest-oaipmh");
        record.setRifCsGroup(rifCsGroup);
        return record;
    }
}

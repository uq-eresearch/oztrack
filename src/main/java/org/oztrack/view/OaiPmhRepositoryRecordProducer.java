package org.oztrack.view;

import java.util.Arrays;
import java.util.Iterator;

public class OaiPmhRepositoryRecordProducer implements OaiPmhRecordProducer {
    @Override
    public Iterator<OaiPmhRecord> iterator() {
        OaiPmhRecord repositoryRecord = new OaiPmhRecord();
        repositoryRecord.setIdentifier("http://oztrack.org/id/repository");
        repositoryRecord.setTitle("OzTrack");
        repositoryRecord.setDescription(
            "OzTrack is a free-to-use web-based platform for analysing and " +
            "visualising individual-based animal location data."
        );
        repositoryRecord.setUrl("http://oztrack.org/");
        repositoryRecord.setCreator("The University of Queensland");
        repositoryRecord.setCreateDate("2011-11-02T03:47:24Z");
        repositoryRecord.setUpdateDate("2011-11-02T03:47:24Z");
        repositoryRecord.setDcType("Service");
        repositoryRecord.setRifCsObjectElemName("service");
        repositoryRecord.setRifCsObjectTypeAttr("report");
        repositoryRecord.setRifCsGroup("OzTrack");
        return Arrays.asList(repositoryRecord).iterator();
    }
}

package org.oztrack.data.access;

import java.util.Date;

import org.oztrack.data.model.types.OaiPmhRecord;
import org.springframework.stereotype.Service;

@Service
public interface OaiPmhRecordDao {
    public OaiPmhRecord getRecordByOaiPmhRecordIdentifier(String identifier);
    public OaiPmhEntityProducer<OaiPmhRecord> getRecords(Date from, Date to, String setSpec);
    void updateOaiPmhSets();
}

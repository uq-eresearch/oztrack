package org.oztrack.data.access;

import org.oztrack.data.model.types.OaiPmhRecord;
import org.springframework.stereotype.Service;

@Service
public interface OaiPmhRecordDao {
    public OaiPmhRecord getRecordByOaiPmhRecordIdentifier(String identifier);
    public OaiPmhEntityProducer<OaiPmhRecord> getRecords();
}

package org.oztrack.data.access;

import org.oztrack.data.model.types.OaiPmhRecord;
import org.springframework.stereotype.Service;

@Service
public interface OaiPmhRecordDao {
    public OaiPmhEntityProducer<OaiPmhRecord> getRecords();
}

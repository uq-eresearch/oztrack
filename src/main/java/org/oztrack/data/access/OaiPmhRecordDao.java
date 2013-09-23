package org.oztrack.data.access;

import org.springframework.stereotype.Service;

@Service
public interface OaiPmhRecordDao {
    OaiPmhRecordProducer getRecords();
}

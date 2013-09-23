package org.oztrack.data.access.impl;

import org.oztrack.data.access.OaiPmhRecordDao;
import org.oztrack.data.access.OaiPmhRecordProducer;
import org.springframework.stereotype.Service;

@Service
public class OaiPmhRecordDaoImpl implements OaiPmhRecordDao {
    @Override
    public OaiPmhRecordProducer getRecords() {
        return new OaiPmhRepositoryRecordProducer();
    }
}

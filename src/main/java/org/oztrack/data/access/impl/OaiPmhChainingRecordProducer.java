package org.oztrack.data.access.impl;

import java.util.Iterator;
import java.util.List;

import org.oztrack.data.access.OaiPmhRecordProducer;
import org.oztrack.data.model.types.OaiPmhRecord;
import org.oztrack.util.ChainingIterator;

public class OaiPmhChainingRecordProducer implements OaiPmhRecordProducer {
    private List<? extends OaiPmhRecordProducer> producers;

    public OaiPmhChainingRecordProducer(List<? extends OaiPmhRecordProducer> producers) {
        this.producers = producers;
    }

    @Override
    public Iterator<OaiPmhRecord> iterator() {
        return new ChainingIterator<OaiPmhRecord>(producers.iterator());
    }

}

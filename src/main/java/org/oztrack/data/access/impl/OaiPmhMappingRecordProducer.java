package org.oztrack.data.access.impl;

import java.util.Iterator;

import org.oztrack.data.access.OaiPmhRecordProducer;
import org.oztrack.data.model.types.OaiPmhRecord;

public abstract class OaiPmhMappingRecordProducer<T> implements OaiPmhRecordProducer {
    private Iterator<T> sourceIterator;

    public OaiPmhMappingRecordProducer(Iterator<T> sourceIterator) {
        this.sourceIterator = sourceIterator;
    }

    protected abstract OaiPmhRecord map(T source);

    @Override
    public Iterator<OaiPmhRecord> iterator() {
        return new Iterator<OaiPmhRecord>() {
            @Override
            public boolean hasNext() {
                return sourceIterator.hasNext();
            }

            @Override
            public OaiPmhRecord next() {
                return map(sourceIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}

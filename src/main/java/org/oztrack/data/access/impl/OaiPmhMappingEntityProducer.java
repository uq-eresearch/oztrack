package org.oztrack.data.access.impl;

import java.util.Iterator;

import org.oztrack.data.access.OaiPmhEntityMapper;
import org.oztrack.data.access.OaiPmhEntityProducer;

public final class OaiPmhMappingEntityProducer<S, T> implements OaiPmhEntityProducer<T> {
    private Iterator<S> sourceIterator;
    private OaiPmhEntityMapper<S, T> mapper;

    public OaiPmhMappingEntityProducer(Iterator<S> sourceIterator, OaiPmhEntityMapper<S, T> mapper) {
        this.sourceIterator = sourceIterator;
        this.mapper = mapper;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return sourceIterator.hasNext();
            }

            @Override
            public T next() {
                return mapper.map(sourceIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}

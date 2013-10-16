package org.oztrack.data.access.impl;

import java.util.Iterator;

import org.oztrack.data.access.OaiPmhEntityProducer;

public abstract class OaiPmhMappingEntityProducer<S, T> implements OaiPmhEntityProducer<T> {
    private Iterator<S> sourceIterator;

    public OaiPmhMappingEntityProducer(Iterator<S> sourceIterator) {
        this.sourceIterator = sourceIterator;
    }

    protected abstract T map(S source);

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return sourceIterator.hasNext();
            }

            @Override
            public T next() {
                return map(sourceIterator.next());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

}

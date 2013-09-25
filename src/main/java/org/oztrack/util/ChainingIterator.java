package org.oztrack.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChainingIterator<T> implements Iterator<T> {
    private Iterator<? extends Iterable<T>> iterators;
    private Iterator<T> currentIterator;

    public ChainingIterator(Iterator<? extends Iterable<T>> iteratorsIterator) {
        this.iterators = iteratorsIterator;
    }

    @Override
    public boolean hasNext() {
        while (((currentIterator == null) || !currentIterator.hasNext()) && iterators.hasNext()) {
            currentIterator = iterators.next().iterator();
        }
        return (currentIterator != null) && currentIterator.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return currentIterator.next();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
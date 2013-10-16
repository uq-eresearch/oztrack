package org.oztrack.data.access.impl;

import java.util.Iterator;
import java.util.List;

import org.oztrack.data.access.OaiPmhEntityProducer;
import org.oztrack.util.ChainingIterator;

public class OaiPmhChainingEntityProducer<T> implements OaiPmhEntityProducer<T> {
    private List<? extends OaiPmhEntityProducer<T>> producers;

    public OaiPmhChainingEntityProducer(List<? extends OaiPmhEntityProducer<T>> producers) {
        this.producers = producers;
    }

    @Override
    public Iterator<T> iterator() {
        return new ChainingIterator<T>(producers.iterator());
    }

}

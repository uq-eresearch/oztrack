package org.oztrack.data.access;

public interface OaiPmhEntityMapper<S, T> {
    public T map(S source);
}

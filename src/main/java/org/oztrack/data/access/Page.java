package org.oztrack.data.access;

import java.util.List;

public class Page<T> {
    private List<T> objects;
    private int offset;
    private int limit;
    private int count;

    public Page(List<T> objects, int offset, int limit, int count) {
        this.objects = objects;
        this.offset = offset;
        this.limit = limit;
        this.count = count;
    }

    public List<T> getObjects() {
        return objects;
    }

    public int getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }

    public int getCount() {
        return count;
    }
}


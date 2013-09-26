package org.oztrack.data.access.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oztrack.data.model.types.OaiPmhRecord;

public class OaiPmhMappingRecordProducerTest {
    private Iterator<OaiPmhRecord> zeroRecordsIterator;
    private Iterator<OaiPmhRecord> manyRecordsIterator;

    @Before
    public void setUp() {
        Iterator<String> zeroStringsIterator = Arrays.<String>asList().iterator();
        OaiPmhMappingRecordProducer<String> zeroRecordsProducer = new OaiPmhMappingRecordProducer<String>(zeroStringsIterator) {
            @Override
            protected OaiPmhRecord map(String source) {
                throw new RuntimeException();
            }
        };
        this.zeroRecordsIterator = zeroRecordsProducer.iterator();

        Iterator<String> manyStringsIterator = Arrays.asList("foo", "bar", "baz").iterator();
        OaiPmhMappingRecordProducer<String> manyRecordsProducer = new OaiPmhMappingRecordProducer<String>(manyStringsIterator) {
            @Override
            protected OaiPmhRecord map(String source) {
                OaiPmhRecord record = new OaiPmhRecord();
                record.setTitle(source);
                return record;
            }
        };
        this.manyRecordsIterator = manyRecordsProducer.iterator();
    }

    @Test
    public void testZeroRecordsContents() {
        Assert.assertFalse(zeroRecordsIterator.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void testZeroRecordsException() {
        zeroRecordsIterator.next();
    }

    @Test
    public void testManyRecordsContents() {
        Assert.assertEquals("foo", manyRecordsIterator.next().getTitle());
        Assert.assertEquals("bar", manyRecordsIterator.next().getTitle());
        Assert.assertEquals("baz", manyRecordsIterator.next().getTitle());
        Assert.assertFalse(manyRecordsIterator.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void testManyRecordsException() {
        manyRecordsIterator.next();
        manyRecordsIterator.next();
        manyRecordsIterator.next();
        manyRecordsIterator.next();
    }
}

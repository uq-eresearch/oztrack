package org.oztrack.data.access.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.oztrack.data.access.OaiPmhEntityMapper;
import org.oztrack.data.model.types.OaiPmhRecord;

public class OaiPmhMappingRecordProducerTest {
    private Iterator<OaiPmhRecord> zeroRecordsIterator;
    private Iterator<OaiPmhRecord> manyRecordsIterator;

    @Before
    public void setUp() {
        Iterator<String> zeroStringsIterator = Arrays.<String>asList().iterator();
        OaiPmhMappingEntityProducer<String, OaiPmhRecord> zeroRecordsProducer =
            new OaiPmhMappingEntityProducer<String, OaiPmhRecord>(zeroStringsIterator, new OaiPmhEntityMapper<String, OaiPmhRecord>() {
                @Override
                public OaiPmhRecord map(String source) {
                    throw new RuntimeException();
                }
            });
        this.zeroRecordsIterator = zeroRecordsProducer.iterator();

        Iterator<String> manyStringsIterator = Arrays.asList("foo", "bar", "baz").iterator();
        OaiPmhMappingEntityProducer<String, OaiPmhRecord> manyRecordsProducer =
            new OaiPmhMappingEntityProducer<String, OaiPmhRecord>(manyStringsIterator, new OaiPmhEntityMapper<String, OaiPmhRecord>() {
                @Override
                public OaiPmhRecord map(String source) {
                    OaiPmhRecord record = new OaiPmhRecord();
                    record.setObjectIdentifier(source);
                    return record;
                }
            });
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
        Assert.assertEquals("foo", manyRecordsIterator.next().getObjectIdentifier());
        Assert.assertEquals("bar", manyRecordsIterator.next().getObjectIdentifier());
        Assert.assertEquals("baz", manyRecordsIterator.next().getObjectIdentifier());
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

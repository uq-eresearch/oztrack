package org.oztrack.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChainingIteratorTest {
    private ChainingIterator<String> emptyChainingIterator;
    private ChainingIterator<String> soloChainingIterator;
    private ChainingIterator<String> trioChainingIterator;

    @Before
    public void setUp() {
        List<Iterable<String>> emptyChainedIterators = new ArrayList<Iterable<String>>();
        this.emptyChainingIterator = new ChainingIterator<String>(emptyChainedIterators.iterator());

        List<Iterable<String>> soloChainedIterators = new ArrayList<Iterable<String>>();
        soloChainedIterators.add(Arrays.asList("foo", "bar", "baz"));
        this.soloChainingIterator = new ChainingIterator<String>(soloChainedIterators.iterator());

        List<Iterable<String>> trioChainedIterators = new ArrayList<Iterable<String>>();
        trioChainedIterators.add(Arrays.asList("foo", "bar"));
        trioChainedIterators.add(Arrays.asList("qux"));
        trioChainedIterators.add(Arrays.<String>asList());
        trioChainedIterators.add(Arrays.asList("baz"));
        trioChainedIterators.add(Arrays.<String>asList());
        this.trioChainingIterator = new ChainingIterator<String>(trioChainedIterators.iterator());
    }

    @Test
    public void testEmptyContents() {
        Assert.assertFalse(emptyChainingIterator.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void testEmptyException() {
        emptyChainingIterator.next();
    }

    @Test
    public void testSoloContents() {
        Assert.assertEquals("foo", soloChainingIterator.next());
        Assert.assertEquals("bar", soloChainingIterator.next());
        Assert.assertEquals("baz", soloChainingIterator.next());
        Assert.assertFalse(soloChainingIterator.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void testSoloException() {
        soloChainingIterator.next();
        soloChainingIterator.next();
        soloChainingIterator.next();
        soloChainingIterator.next();
    }

    @Test
    public void testTrioContents() {
        Assert.assertEquals("foo", trioChainingIterator.next());
        Assert.assertEquals("bar", trioChainingIterator.next());
        Assert.assertEquals("qux", trioChainingIterator.next());
        Assert.assertEquals("baz", trioChainingIterator.next());
        Assert.assertFalse(trioChainingIterator.hasNext());
    }

    @Test(expected=NoSuchElementException.class)
    public void testTrioException() {
        trioChainingIterator.next();
        trioChainingIterator.next();
        trioChainingIterator.next();
        trioChainingIterator.next();
        trioChainingIterator.next();
    }
}

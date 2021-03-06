/*
 * File:                CollectionUtilTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 07, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Tests of CollectionUtil
 *
 * @author Justin Basilico
 * @since 2.1
 */
public class CollectionUtilTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param testName The test name.
     */
    public CollectionUtilTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Random number generator
     */
    public final Random RANDOM = new Random(1);

    /**
     * An Iterable that isn't a Collection
     */
    public static class PureIterable
        implements Iterable<Integer>
    {

        public PureIterable()
        {
        }

        public PureIterable(int endValue)
        {
            this.endValue = endValue;
        }

        /**
         * End value, {@value}
         */
        int endValue = 10;

        public Iterator<Integer> iterator()
        {
            return new Iter();
        }

        /**
         * Iterator
         */
        public class Iter
            implements Iterator<Integer>
        {

            /**
             * Index
             */
            int index = 0;

            public boolean hasNext()
            {
                return (this.index < endValue);
            }

            public Integer next()
            {
                return this.index++;
            }

            public void remove()
            {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        }

    }

    /**
     * Constructors
     */
    public void testConstructors()
    {
        System.out.println("Constructors");

        CollectionUtil cu = new CollectionUtil();
        assertNotNull(cu);
    }

    /**
     * Test of isEmpty method, of class CollectionUtil.
     */
    public void testIsEmpty()
    {
        Collection<Object> collection = null;
        assertTrue(CollectionUtil.isEmpty(collection));

        collection = new LinkedList<Object>();
        assertTrue(CollectionUtil.isEmpty(collection));

        collection.add("a");
        assertFalse(CollectionUtil.isEmpty(collection));

        collection.add("b");
        assertFalse(CollectionUtil.isEmpty(collection));

        collection.clear();
        assertTrue(CollectionUtil.isEmpty(collection));

        Iterable<Object> iterable = null;
        assertTrue(CollectionUtil.isEmpty(iterable));

        iterable = collection;
        assertTrue(CollectionUtil.isEmpty(iterable));

        collection.add("a");
        assertFalse(CollectionUtil.isEmpty(iterable));

        collection.add("b");
        assertFalse(CollectionUtil.isEmpty(iterable));

        collection.clear();
        assertTrue(CollectionUtil.isEmpty(iterable));

        Iterable<?> i2 = new PureIterable();
        assertFalse(CollectionUtil.isEmpty(i2));

    }

    /**
     * Test of size method, of class CollectionUtil.
     */
    public void testSize()
    {
        Collection<Object> collection = null;
        int size = 0;
        assertEquals(size, CollectionUtil.size(collection));

        collection = new LinkedList<Object>();
        assertEquals(size, CollectionUtil.size(collection));

        collection.add("a");
        size = 1;
        assertEquals(size, CollectionUtil.size(collection));

        collection.add("b");
        size = 2;
        assertEquals(size, CollectionUtil.size(collection));

        collection.clear();
        size = 0;
        assertEquals(size, CollectionUtil.size(collection));

        Iterable<Object> iterable = null;
        assertEquals(size, CollectionUtil.size(iterable));

        iterable = collection;
        assertEquals(size, CollectionUtil.size(iterable));

        collection.add("a");
        size = 1;
        assertEquals(size, CollectionUtil.size(iterable));

        collection.add("b");
        size = 2;
        assertEquals(size, CollectionUtil.size(iterable));

        collection.clear();
        size = 0;
        assertEquals(size, CollectionUtil.size(iterable));

        Iterable<?> i2 = new PureIterable();
        assertEquals(10, CollectionUtil.size(i2));

    }

    /**
     * Test of getFirst method, of class CollectionUtil.
     */
    public void testGetFirst()
    {
        Collection<Object> collection = null;
        Object first = null;
        assertSame(first, CollectionUtil.getFirst(collection));

        collection = new LinkedList<Object>();
        assertSame(first, CollectionUtil.getFirst(collection));

        collection.add("a");
        first = "a";
        assertSame(first, CollectionUtil.getFirst(collection));

        collection.add("b");
        assertSame(first, CollectionUtil.getFirst(collection));

        collection.clear();
        first = null;
        assertSame(first, CollectionUtil.getFirst(collection));

        Iterable<Object> iterable = null;
        assertSame(first, CollectionUtil.getFirst(iterable));

        iterable = collection;
        assertSame(first, CollectionUtil.getFirst(iterable));

        collection.add("a");
        first = "a";
        assertSame(first, CollectionUtil.getFirst(iterable));

        collection.add("b");
        assertSame(first, CollectionUtil.getFirst(iterable));

        collection.clear();
        first = null;
        assertSame(first, CollectionUtil.getFirst(iterable));

        List<Object> list = null;
        first = null;
        assertSame(first, CollectionUtil.getFirst(list));

        list = new LinkedList<Object>();
        assertSame(first, CollectionUtil.getFirst(list));

        first = "a";
        list.add(first);
        assertSame(first, CollectionUtil.getFirst(list));

        list.add("b");
        assertSame(first, CollectionUtil.getFirst(list));
    }

    /**
     * Test of getLast method, of class CollectionUtil.
     */
    public void testGetLast()
    {
        List<Object> list = null;
        Object last = null;
        assertSame(last, CollectionUtil.getLast(list));

        list = new LinkedList<Object>();
        assertSame(last, CollectionUtil.getLast(list));

        last = "a";
        list.add(last);
        assertSame(last, CollectionUtil.getLast(list));

        last = "b";
        list.add("b");
        assertSame(last, CollectionUtil.getLast(list));

        last = null;
        list.add(null);
        assertSame(last, CollectionUtil.getLast(list));
    }

    /**
     * Test of equals method, of class CollectionUtil.
     */
    public void testEquals()
    {
        List<Integer> srcList = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        List<Integer> c1 = new ArrayList<>(srcList);
        List<Object> c2 = new ArrayList<>(srcList);
        Iterable<Integer> i3 = new PureIterable();
        Iterable<Integer> i4 = new PureIterable();

        assertTrue(CollectionUtil.equals(c1, c2));
        assertTrue(CollectionUtil.equals(c2, i3));
        assertTrue(CollectionUtil.equals(i3, i4));
        assertTrue(CollectionUtil.equals(i4, c1));

        c1.set(9, 10);
        assertFalse(CollectionUtil.equals(c1, c2));

        c1.remove(9);
        i3 = new PureIterable(9);
        assertFalse(CollectionUtil.equals(c1, c2));
        assertFalse(CollectionUtil.equals(i3, i4));
        assertTrue(CollectionUtil.equals(c1, i3));
    }

    /**
     * Test of createSequentialPartitions method
     */
    public void testCreateSequentialPartitions()
    {
        System.out.println("createPartition1");
        int numData = 100;
        ArrayList<Double> data = new ArrayList<Double>(numData);
        for (int i = 0; i < numData; i++)
        {
            data.add(new Double(RANDOM.nextDouble()));
        }

        int numPartitions = 1;

        ArrayList<List<? extends Double>> r1
            = CollectionUtil.createSequentialPartitions(data, numPartitions);
        assertEquals(1, r1.size());
        assertEquals(numData, r1.get(0).size());
        for (int i = 0; i < numData; i++)
        {
            assertEquals(data.get(i), r1.get(0).get(i));
        }

        numPartitions = 3;
        ArrayList<List<? extends Double>> r2
            = CollectionUtil.createSequentialPartitions(data, numPartitions);
        assertEquals(numPartitions, r2.size());
        assertEquals(numData / numPartitions, r2.get(0).size());
        assertEquals(numData / numPartitions, r2.get(1).size());
        assertEquals(34, r2.get(2).size());
        Iterator<Double> id = data.iterator();
        int index = 0;
        for (List<? extends Double> partition : r2)
        {
            for (int p = 0; p < partition.size(); p++)
            {
                assertEquals(id.next(), partition.get(p));
                index++;
            }
        }

    }

    public void testFindKthLargest()
    {
        System.out.println("findKthLargest");

        ArrayList<Double> values = new ArrayList<Double>(
            Arrays.asList(-1.0, 4.0, -2.0, 3.0, 5.0, 0.0, 1.0, 1.0));

        for (int k = 0; k < values.size(); k++)
        {
            this.testFindKthLargest(values, k);
        }

    }

    protected void testFindKthLargest(
        ArrayList<Double> values,
        int k)
    {

        int[] indices = CollectionUtil.findKthLargest(k, values,
            NumberComparator.INSTANCE);

        ArrayList<Double> sortedList = new ArrayList<Double>(values);
        Collections.sort(sortedList);
        double expected = sortedList.get(k);
        assertEquals(expected, values.get(indices[k]));

        for (int i = 0; i < k; i++)
        {
            assertTrue(values.get(indices[i]) <= expected);
        }
        for (int i = k + 1; i < values.size(); i++)
        {
            assertTrue(values.get(indices[i]) >= expected);
        }

    }

    public void testAsArrayList()
    {
        System.out.println("asArrayList");

        List<Double> values = Arrays.asList(1.0, 2.0, 3.0, 4.0);
        System.out.println("Class: " + values.getClass());
        ArrayList<Double> a0 = CollectionUtil.asArrayList(values);
        assertNotSame(values, a0);
        assertEquals(values.size(), a0.size());
        for (int i = 0; i < values.size(); i++)
        {
            assertSame(values.get(i), a0.get(i));
        }

        ArrayList<Double> a1 = CollectionUtil.asArrayList(a0);
        assertSame(a1, a0);

        assertNull(CollectionUtil.asArrayList(null));

    }

    /**
     * Tests ObjectUtil.getElement
     */
    public void testGetElement()
    {
        System.out.println("getElement");

        List<Double> i1 = Arrays.asList(
            RANDOM.nextGaussian(), RANDOM.nextGaussian(), RANDOM.nextGaussian());

        assertSame(i1.get(0), CollectionUtil.getElement(i1, 0));
        assertSame(i1.get(1), CollectionUtil.getElement(i1, 1));
        assertSame(i1.get(2), CollectionUtil.getElement(i1, 2));

        try
        {
            CollectionUtil.getElement(i1, -1);
            fail("Index must be >= 0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            CollectionUtil.getElement(i1, i1.size());
            fail("Index must be < size");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        Iterable<Integer> i2 = new PureIterable();
        assertEquals(new Integer(0), CollectionUtil.getElement(i2, 0));
        assertEquals(new Integer(1), CollectionUtil.getElement(i2, 1));
        assertEquals(new Integer(5), CollectionUtil.getElement(i2, 5));

        try
        {
            CollectionUtil.getElement(i2, -1);
            fail("Index must be >= 0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            CollectionUtil.getElement(i2, CollectionUtil.size(i2));
            fail("Index must be < size");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

    }

    /**
     * Tests ObjectUtil.removeElement
     */
    public void testRemoveElement()
    {
        System.out.println("removeElement");

        List<Double> i1 = new ArrayList<Double>();
        i1.add(RANDOM.nextGaussian());
        i1.add(RANDOM.nextGaussian());
        i1.add(RANDOM.nextGaussian());

        List<Double> i1Copy = new ArrayList<Double>(i1);

        try
        {
            CollectionUtil.removeElement(i1, -1);
            fail("Index must be >= 0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        try
        {
            CollectionUtil.removeElement(i1, i1.size());
            fail("Index must be < size");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }

        assertSame(i1Copy.get(0), CollectionUtil.removeElement(i1, 0));
        assertEquals(2, CollectionUtil.size(i1));
        assertSame(i1Copy.get(1), CollectionUtil.removeElement(i1, 0));
        assertEquals(1, CollectionUtil.size(i1));
        assertSame(i1Copy.get(2), CollectionUtil.removeElement(i1, 0));
        assertEquals(0, CollectionUtil.size(i1));

        HashSet<Integer> i2 = new HashSet<Integer>();
        for (int i = 0; i < 10; i++)
        {
            i2.add(i);
        }

        assertEquals(10, CollectionUtil.size(i2));
        assertEquals(new Integer(5), CollectionUtil.removeElement(i2, 5));
        assertEquals(9, CollectionUtil.size(i2));
        assertEquals(new Integer(1), CollectionUtil.removeElement(i2, 1));
        assertEquals(8, CollectionUtil.size(i2));
        assertEquals(new Integer(0), CollectionUtil.removeElement(i2, 0));
        assertEquals(7, CollectionUtil.size(i2));
        assertEquals(new Integer(2), CollectionUtil.removeElement(i2, 0));
        assertEquals(6, CollectionUtil.size(i2));

        try
        {
            CollectionUtil.removeElement(i2, -1);
            fail("Index must be >= 0");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
        assertEquals(6, CollectionUtil.size(i2));

        try
        {
            CollectionUtil.removeElement(i2, CollectionUtil.size(i2));
            fail("Index must be < size");
        }
        catch (Exception e)
        {
            System.out.println("Good: " + e);
        }
        assertEquals(6, CollectionUtil.size(i2));

    }

    public void testToStringDelimited()
    {
        List<String> list = null;
        assertEquals("null", CollectionUtil.toStringDelimited(list, "-"));

        list = new ArrayList<String>();
        assertEquals("", CollectionUtil.toStringDelimited(list, "-"));

        list.add("a");
        assertEquals("a", CollectionUtil.toStringDelimited(list, "-"));

        list.add("bb");
        assertEquals("a-bb", CollectionUtil.toStringDelimited(list, "-"));

        list.add("c");
        assertEquals("a=bb=c", CollectionUtil.toStringDelimited(list, "="));

        list.add("dzd");
        assertEquals("a-/-bb-/-c-/-dzd", CollectionUtil.toStringDelimited(list,
            "-/-"));
    }

    public void testCreateArrayList()
    {
        Double first = RANDOM.nextGaussian();
        Double second = RANDOM.nextGaussian();

        ArrayList<Double> result = CollectionUtil.createArrayList(first, second);
        assertEquals(2, result.size());
        assertSame(first, result.get(0));
        assertSame(second, result.get(1));

        first = null;

        result = CollectionUtil.createArrayList(first, second);
        assertEquals(2, result.size());
        assertSame(first, result.get(0));
        assertSame(second, result.get(1));

        second = null;
        result = CollectionUtil.createArrayList(first, second);
        assertEquals(2, result.size());
        assertSame(first, result.get(0));
        assertSame(second, result.get(1));

        first = RANDOM.nextGaussian();
        result = CollectionUtil.createArrayList(first, second);
        assertEquals(2, result.size());
        assertSame(first, result.get(0));
        assertSame(second, result.get(1));
    }

    public void testCreateHashMapWithSize()
    {
        HashMap<String, Double> result
            = CollectionUtil.createHashMapWithSize(10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertNotSame(result, CollectionUtil.createHashMapWithSize(10));
        assertEquals(result, CollectionUtil.createHashMapWithSize(10));
    }

    public void testCreateLinkedHashMapWithSize()
    {
        LinkedHashMap<String, Double> result
            = CollectionUtil.createLinkedHashMapWithSize(10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertNotSame(result, CollectionUtil.createLinkedHashMapWithSize(10));
        assertEquals(result, CollectionUtil.createLinkedHashMapWithSize(10));
    }

    public void testCreateHashSetWithSize()
    {
        HashSet<String> result = CollectionUtil.createHashSetWithSize(10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertNotSame(result, CollectionUtil.createHashSetWithSize(10));
        assertEquals(result, CollectionUtil.createHashSetWithSize(10));
    }

    public void testCreateLinkedHashSetWithSize()
    {
        LinkedHashSet<String> result
            = CollectionUtil.createLinkedHashSetWithSize(10);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        assertNotSame(result, CollectionUtil.createLinkedHashSetWithSize(10));
        assertEquals(result, CollectionUtil.createLinkedHashSetWithSize(10));
    }

}

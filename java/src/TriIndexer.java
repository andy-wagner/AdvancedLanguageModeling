package edu.berkeley.nlp.assignments.assign1.student;


import java.io.Serializable;
import java.util.AbstractList;
import java.util.Arrays;

/**
 * Maintains a two-way map between a set of objects and contiguous integers from
 * 0 to the number of objects. Use get(i) to look up object i, and
 * indexOf(object) to look up the index of an object.
 *
 * @author Dan Klein
 */
public class TriIndexer extends AbstractList<Long> implements Serializable
{
    private static final long serialVersionUID = -8769544079136550516L;

    int CountSize_;

    int[] Count;
    int CountHead;

    OpenHashMapTrigram indexes;

    /**
     * Return the object with the given index
     *
     * @param index
     */
    public int getCount(int index) {
        return Count[index];
    }

    /**
     * Returns True if it had to add another element to Count. False if the index already existed
     */



    /**
     * Returns the number of objects indexed.
     */
    @Override
    public int size() {
        return CountHead;
    }


    /**
     * Returns the index of the given object, or -1 if the object is not present
     * in the indexer.
     *
     * @param o
     * @return
     */
    @Override
    public int indexOf(Object o) {
        long index = indexes.get((long)(o));

        return Math.toIntExact(index);
    }

    /**
     * Add an element to the indexer if not already present. In either case,
     * returns the index of the given object.
     *
     * @param e
     * @return
     */
    public int addAndGetIndex(long e) {
        long index = indexes.get(e);
        if (index >= 0) { return Math.toIntExact(index); }
        //  Else, add
        int newIndex = CountHead;
        indexes.put(e, newIndex);
        CountHead++;
        return newIndex;
    }

    /**
     * Add an element to the indexer. If the element is already in the indexer,
     * the indexer is unchanged (and false is returned).
     *
     * @param e
     * @return
     */
    @Override
    public boolean add(Long e) {
        return addAndGetIndex(e) == size() - 1;
    }

    @Override
    public Long get(int index) {
        return (long)(Count[index]);
    }

    public TriIndexer(int CountSize, int OpenHashMapInitSize, double OpenHashMapLoadFactor) {
        CountSize_ = CountSize;
        Count = new int[CountSize_];
        CountHead = 0;
        indexes = new OpenHashMapTrigram(OpenHashMapInitSize, OpenHashMapLoadFactor);
    }

}

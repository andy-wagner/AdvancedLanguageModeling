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
public class BiIndexer implements Serializable
{
    private static final long serialVersionUID = -8769544079136550516L;

    int CountSize_;
    int columns_;

    int[][] Count;
    int CountHead; // only one count head because we are using the same index for all grams

    OpenHashMapBigram indexes;

    /**
     * Return the object with the given index
     *
     * @param index
     */
    public int getCount(int index, int column) {
        return Count[index][column];
    }

    /**
     * Returns True if it had to add another element to Count. False if the index already existed
     */



    /**
     * Returns the number of objects indexed.
     */
    public int size() {
        return CountHead;
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
    public boolean add(long e) {
        return addAndGetIndex(e) == size() - 1;
    }

    public long get(int index, int column) {
        return (Count[index][column]);
    }

    public BiIndexer(int CountSize, int columns, int OpenHashMapInitSize, double OpenHashMapLoadFactor) {
        CountSize_ = CountSize;
        columns_ = columns;
        Count = new int[CountSize_][columns_];
        CountHead = 0;
        indexes = new OpenHashMapBigram(OpenHashMapInitSize, .9);
    }

}

package edu.berkeley.nlp.assignments.assign1.student;

import java.util.Arrays;

public class OpenHashMap {

    private long[] keys;

    private int[] values;

    private int size = 0;

    private final int EMPTY_KEY = -1;

    private final double MAX_LOAD_FACTOR;

    public boolean put(long k, int v) {
        if (size / (double) keys.length > MAX_LOAD_FACTOR) {
            rehash();
        }
        return putHelp(k, v, keys, values);

    }

    public OpenHashMap() {
        this(10);
    }

    public OpenHashMap(int initialCapacity_) {
        this(initialCapacity_, 0.7);
    }

    public OpenHashMap(int initialCapacity_, double loadFactor) {
        int cap = Math.max(5, (int) (initialCapacity_ / loadFactor));
        MAX_LOAD_FACTOR = loadFactor;
        values = new int[cap];
        Arrays.fill(values, -1);
        keys = new long[cap];
        Arrays.fill(keys, -1);
    }

    /**
     *
     */
    private void rehash() {
        System.out.println(String.format("Rehashing for hash table with values length %s", values.length));
        long[] newKeys = new long[(int)(keys.length * 3 / 2)];
        int[] newValues = new int[(int)(values.length * 3 / 2)];
        Arrays.fill(newValues, -1);
        Arrays.fill(newKeys, -1);
        size = 0;
        for (int i = 0; i < keys.length; ++i) {
            long curr = keys[i];
            if (curr != -1) {
                int val = values[i];
                putHelp(curr, val, newKeys, newValues);
            }
        }
        keys = newKeys;
        values = newValues;
    }

    /**
     * @param k
     * @param v
     */
    private boolean putHelp(long k, int v, long[] keyArray, int[] valueArray) {
        long pos = getInitialPos(k, keyArray);
        long curr = keyArray[Math.toIntExact(pos)];
        while (curr != -1 && !(curr == k)) {
            pos++;
            if (pos == keyArray.length) pos = 0;
            curr = keyArray[Math.toIntExact(pos)];
        }

        valueArray[Math.toIntExact(pos)] = v;
        if (curr == -1) {
            size++;
            keyArray[Math.toIntExact(pos)] = k;
            return true;
        }
        return false;
    }

    /**
     * @param k
     * @param keyArray
     * @return
     */
    private long getInitialPos(long k, long[] keyArray) {
        long hash = (k ^ (k >>> 32)) * 3875239;
        long pos = hash % keyArray.length;
        if (pos < 0) pos += keyArray.length;
        // N.B. Doing it this old way causes Integer.MIN_VALUE to be
        // handled incorrect since -Integer.MIN_VALUE is still
        // Integer.MIN_VALUE
//		if (hash < 0) hash = -hash;
//		int pos = hash % keyArray.length;
        return pos;
    }

    public int get(long key) {
        long pos = find(key);
        return values[Math.toIntExact(pos)];
    }

    /**
     * @param k
     * @return
     */
    private long find(long k) {
        long pos = getInitialPos(k, keys);
        long curr = keys[Math.toIntExact(pos)];
        while (curr != -1 && !(curr == k)) {
            pos++;
            if (pos == keys.length) pos = 0;
            curr = keys[Math.toIntExact(pos)];
        }
        return pos;
    }

    public void increment(long k, int c) {
        long pos = find(k);
        long currKey = keys[Math.toIntExact(pos)];
        if (currKey == -1) {
            put(k, c);
        } else
            values[Math.toIntExact(pos)]++;
    }

}

package com.renaud.larp.server.storage;

import java.util.List;

/**
 * The unique operations we need to do on the storage backend.
 */
public abstract class AbstractStorage {

    /**
     * Increment the counter with the key aKey by one.
     * @param aKey The key to increment.
     */
    public abstract void increment(final String aKey);

    /**
     * List of all elements as LogLine
     * @see LogLine
     * @return The list of LogLine extracted from backend.
     */
    public abstract List<LogLine> elements();

    /**
     *
     * @param aKey
     * @return
     */
    public int get(final String aKey) {
        int total = 0;
        for(final LogLine line: this.elements()){
            if(line.getQuery().equals(aKey)){
                total += line.getCount();
            }
        }
        return total;
    }
}

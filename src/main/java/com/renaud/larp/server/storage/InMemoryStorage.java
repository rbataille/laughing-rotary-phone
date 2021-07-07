package com.renaud.larp.server.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Use local memory to store data.
 * @see AbstractStorage
 */
public class InMemoryStorage extends AbstractStorage {
    private final Map<String, Integer> map = new ConcurrentHashMap<>();


    /**
     * @see AbstractStorage#increment(String)
     * @param aKey The key to increment.
     */
    @Override
    public void increment(final String aKey) {
        synchronized (this.map) {
            int value = 1;
            if (this.map.containsKey(aKey)) {
                final int previousValue = this.map.get(aKey);
                value = previousValue+1;
            }
            this.map.put(aKey, value);
        }
    }

    public void add(final String aKey, final int aValue){
        synchronized (this.map) {
            int value = aValue;
            if (this.map.containsKey(aKey)) {
                final int previousValue = this.map.get(aKey);
                value = value+previousValue;
            }
            this.map.put(aKey, value);
        }
    }


    /**
     * @see AbstractStorage#elements()
     */
    @Override
    public List<LogLine> elements() {
        final List<LogLine> output = new ArrayList<>();
        for(final Map.Entry<String, Integer> items: this.map.entrySet()){
            output.add(new LogLine(items.getKey(), items.getValue()));
        }
        return output;
    }
}

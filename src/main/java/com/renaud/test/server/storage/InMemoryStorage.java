package com.renaud.test.server.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStorage implements IStorage {
    private final Map<String, Integer> map = new ConcurrentHashMap<>();

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

    @Override
    public int get(final String aKey) {
        if (!this.map.containsKey(aKey)) {
            return 0;
        }
        return this.map.get(aKey);
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

    @Override
    public List<LogLine> elements() {
        final List<LogLine> output = new ArrayList<>();
        for(final Map.Entry<String, Integer> items: this.map.entrySet()){
            output.add(new LogLine(items.getKey(), items.getValue()));
        }
        return output;
    }
}

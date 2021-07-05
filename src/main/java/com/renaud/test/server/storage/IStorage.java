package com.renaud.test.server.storage;

import java.util.List;

public interface IStorage {
    void increment(final String aKey);

    int get(final String aKey);

    List<LogLine> elements();
}

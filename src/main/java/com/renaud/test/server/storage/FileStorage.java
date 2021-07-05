package com.renaud.test.server.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorage implements IStorage {
    private final ConcurrentHashMap<String, Integer> counters = new ConcurrentHashMap<>();
    private final String filePath;
    private FileTime lastAccess = FileTime.fromMillis(System.currentTimeMillis());


    public FileStorage(final String aFilePath) {
        this.filePath = aFilePath;
    }

    private void loadElementsFromFile() {
        try {
            final List<String> lines = Files.readAllLines(Paths.get(this.filePath));
            for (final String line : lines) {
                final String cleanedLine = line.trim();
                if (!cleanedLine.contains(":")) {
                    this.increment(cleanedLine, true);
                    continue;
                }
                final String[] parts = line.split(":");
                if (parts.length == 2) {
                    final String key = parts[0];
                    final int count = Integer.parseInt(parts[1]);
                    this.add(key, count, true);
                }
            }
        } catch (final IOException ignored) {
        } finally {
            this.lastAccess = FileTime.fromMillis(System.currentTimeMillis());
        }
    }


    public int get(final String aKey) {
        this.reload();
        if (!this.counters.containsKey(aKey)) {
            return 0;
        }
        return this.counters.get(aKey);
    }

    @Override
    public List<LogLine> elements() {
        this.reload();
        final List<LogLine> output = new ArrayList<>();
        for (final Map.Entry<String, Integer> items : this.counters.entrySet()) {
            output.add(new LogLine(items.getKey(), items.getValue()));
        }
        return output;
    }

    private void reload() {
        this.counters.clear();
        this.loadElementsFromFile();
    }

    public void increment(final String aKey) {
        this.increment(aKey, false);
    }

    private void increment(final String aKey, final boolean skipFlush) {
        this.add(aKey, 1, skipFlush);
    }

    private void add(final String aKey, final int aValue, final boolean skipFlushToDisk) {
        synchronized (this.counters) {
            int valueToStore = aValue;
            if (this.counters.containsKey(aKey)) {
                valueToStore += this.counters.get(aKey);
            }
            this.counters.put(aKey, valueToStore);
        }
        if (skipFlushToDisk) {
            return;
        }
        this.lastAccess = FileTime.fromMillis(System.currentTimeMillis());
        try (final FileWriter writer = new FileWriter(this.filePath, true)) {
            writer.append(aKey).append("\n");
            Files.setLastModifiedTime(Paths.get(this.filePath), this.lastAccess);
        } catch (final IOException ignored) {
            // Do something
        }
    }
}

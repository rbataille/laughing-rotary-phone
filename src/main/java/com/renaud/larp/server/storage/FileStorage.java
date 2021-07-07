package com.renaud.larp.server.storage;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provide a storage using local Filesystem.
 * @see AbstractStorage
 */
public class FileStorage extends AbstractStorage {
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


    /**
     * @see AbstractStorage#elements()
     */
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
        // @TODO conditionner le reload a un changement de date d'acces au fichier ?
        this.counters.clear();
        this.loadElementsFromFile();
    }

    /**
     * @see AbstractStorage#increment(String)
     * @param aKey The key to increment.
     */
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
            // @todo ajouter a une liste "en attente", qui peut etre traitée plus tard (checker nb erreurs etc...)
        }
    }
}

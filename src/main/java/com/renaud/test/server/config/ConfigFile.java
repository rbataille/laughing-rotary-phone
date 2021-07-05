package com.renaud.test.server.config;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This object are used to load config files.
 */
public class ConfigFile {
    /**
     * Current file path.
     */
    private final String filePath;
    /**
     * Properties of the config file.
     */
    private final Map<String, String> properties = new HashMap<>();

    public ConfigFile(final String aFilePath) {
        this.filePath = aFilePath;
    }

    ConfigFile(final Map<String, String> properties){
        this.filePath = "";
        this.properties.putAll(properties);
    }

    public ConfigFile() {
        this("");
    }

    public static ConfigFile empty() {
        return new ConfigFile();
    }


    public void set(final EnumConfig aKey, final String aValue){
        this.properties.put(aKey.getKey(), aValue);
    }

    public void set(final EnumConfig aKey, final EnumConfig.EnumStorageType storageType){
        this.set(aKey, storageType.toString());
    }

    /**
     * read configs from file.
     */
    public void processRead() {
        if (this.filePath.isEmpty()) {
            return;
        }
        final List<String> lines = new ArrayList<>();
        try {
            lines.addAll(Files.readAllLines(Paths.get(this.filePath)));
        }catch (final IOException ignored){
            // ignored
        }
        for (final String line : lines) {
            final String cleanedLine = line.trim();
            if (!cleanedLine.contains("=")) {
                continue;
            }
            final String[] parts = cleanedLine.split("=");
            final String propertyKey = parts[0].trim();
            final String propertyValue = parts[1].trim();
            this.properties.put(propertyKey, propertyValue);
        }

    }

    /**
     * Retrieve a String option with the target key.
     *
     * @param aKey Key of the option to retrieve.
     * @return The option's value.
     */
    public String getString(final String aKey, final String defaultValue) {
        if (!this.properties.containsKey(aKey)) {
            return defaultValue;
        }
        return this.properties.get(aKey);
    }

    /**
     * Retrieve a String option with the target key.
     *
     * @param selectedEnumConfig Then selected enumConfig.
     * @return The option's value.
     */
    public String getString(final EnumConfig selectedEnumConfig) {
        return this.getString(selectedEnumConfig.getKey(), String.valueOf(selectedEnumConfig.getDefaultValue()));
    }

    /**
     * Retrieve an Integer option with the target key.
     *
     * @param aKey Key of the option to retrieve;
     * @return The option's value as int.
     */
    public int getInt(final String aKey, final Integer defaultValue) {
        final String value = this.getString(aKey, defaultValue.toString());
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            return defaultValue;

        }
    }


    /**
     * Retrieve an Integer option with the target key, if the key doesn't exists, return the default value.
     *
     * @param aKey Key of the option to retrieve.
     * @return The option's value as integer or the default value defined in the EnumConfig object.
     */
    public Integer getInt(final EnumConfig aKey) {
        return this.getInt(aKey, (Integer) aKey.getDefaultValue());
    }

    /**
     * Retrieve an Integer option with the target key, if the key doesn't exists, return the default value.
     *
     * @param aKey          Key of the option to retrieve.
     * @param aDefaultValue A default value.
     * @return The option's value as int or the default value.
     */
    public Integer getInt(final EnumConfig aKey, final Integer aDefaultValue) {
        return this.getInt(aKey.getKey(), aDefaultValue);
    }
}

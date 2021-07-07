package com.renaud.larp.server.config;

/**
 * Enum that store all config options availables.
 */
public enum EnumConfig {
    /**
     * The current http server port default to 8000.
     */
    SERVER_PORT("server.port", 8000),
    /**
     * Storage type (available: sqlite|memory|file, default: memory)
     */
    STORAGE_TYPE("storage.type", EnumStorageType.SQLITE),
    /**
     * Sqlite file to store data.
     */
    SQLITE_FILE("sqlite.file", "data/application.sql"),
    /**
     * Sqlite file to store data.
     */
    SQLITE_TABLE("sqlite.table", "logs"),
    /**
     * Sqlite file to store data.
     */
    SQLITE_DB("sqlite.db", "application"),
    /**
     * The current http server port default to 8000.
     */
    STORAGE_FILE("storage.file", "./data/application.sql");

    /**
     * Config key.
     */
    private final String key;
    /**
     * Default value.
     */
    private final Object defaultValue;

    /**
     * Basic constructor.
     *
     * @param aKey          String key.
     * @param aDefaultValue Object default value.
     */
    EnumConfig(final String aKey, final Object aDefaultValue) {
        this.key = aKey;
        this.defaultValue = aDefaultValue;
    }

    /**
     * Return the default value.
     *
     * @return Object.
     */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Return the current config key.
     *
     * @return The config key.
     */
    public String getKey() {
        return this.key;
    }

    public enum EnumStorageType {
        MEMORY("memory"),
        FILE("file"),
        SQLITE("sqlite");

        private final String type;
        EnumStorageType(final String type) {
            this.type = type;
        }
        @Override
        public String toString() {
            return this.type;
        }
    }
}

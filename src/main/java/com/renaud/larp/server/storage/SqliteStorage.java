package com.renaud.larp.server.storage;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.renaud.larp.server.config.ConfigFile;
import com.renaud.larp.server.config.EnumConfig;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SqliteStorage extends AbstractStorage {

    private static ComboPooledDataSource dataSource;
    private final ConfigFile configFile;

    /**
     *
     * @param configFile Config file, used to retrieve config properties (STORAGE_FILE|SQLITE_TABLE)
     */
    public SqliteStorage(final ConfigFile configFile) {
        this.configFile = configFile;
    }

    /**
     *
     * @param storage Current object, (used to retrieve the app configuration.)
     * @return The connection.
     * @throws SQLException Oupps
     * @throws PropertyVetoException Oupps
     */
    static Connection getConnection(final SqliteStorage storage) throws SQLException, PropertyVetoException {
        if (SqliteStorage.dataSource == null) {
            SqliteStorage.dataSource = new ComboPooledDataSource();
        }
        final String sqliteFile = storage.configFile.getString(EnumConfig.STORAGE_FILE);
        SqliteStorage.dataSource.setDriverClass("org.sqlite.JDBC");
        SqliteStorage.dataSource.setJdbcUrl("jdbc:sqlite:" + sqliteFile);
        return SqliteStorage.dataSource.getConnection();
    }

    /**
     * @see AbstractStorage#increment(String)
     * @param aKey The key to increment.
     */
    @Override
    public void increment(final String aKey) {
        try (final Connection connection = SqliteStorage.getConnection(this)) {
            final String tableName = this.configFile.getString(EnumConfig.SQLITE_TABLE);
            try (final PreparedStatement statement = connection.prepareStatement("INSERT INTO " + tableName + " (`query`, `count`)  VALUES(?, 1)")) {
                statement.setString(1, aKey);
                statement.execute();
            }
        } catch (final SQLException | PropertyVetoException ignored) {
        }
    }


    /**
     * @see AbstractStorage#elements()
     */
    @Override
    public List<LogLine> elements() {
        final InMemoryStorage storage = new InMemoryStorage();
        try (final Connection connection = SqliteStorage.getConnection(this)) {
            final String tableName = this.configFile.getString(EnumConfig.SQLITE_TABLE);
            try  (final PreparedStatement statement = connection.prepareStatement("SELECT query, SUM(count) as total FROM " + tableName + " GROUP BY query ORDER BY total DESC")) {
                final ResultSet results = statement.executeQuery();
                while (results.next()) {
                    final String query = results.getString("query");
                    storage.add(query, results.getInt("total"));
                }
                results.close();
            }
        } catch (final SQLException | PropertyVetoException ignored) {
        }
        return storage.elements();
    }
}

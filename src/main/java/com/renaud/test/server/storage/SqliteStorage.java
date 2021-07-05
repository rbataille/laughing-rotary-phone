package com.renaud.test.server.storage;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.renaud.test.server.config.ConfigFile;
import com.renaud.test.server.config.EnumConfig;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SqliteStorage implements IStorage {

    private static ComboPooledDataSource dataSource;
    private final ConfigFile configFile;

    public SqliteStorage(final ConfigFile configFile) {
        this.configFile = configFile;
    }


    static Connection getConnection(final SqliteStorage storage) throws SQLException, PropertyVetoException {
        if (SqliteStorage.dataSource == null) {
            SqliteStorage.dataSource = new ComboPooledDataSource();
        }
        final String sqliteFile = storage.configFile.getString(EnumConfig.SQLITE_FILE);
        SqliteStorage.dataSource.setDriverClass("org.sqlite.JDBC");
        SqliteStorage.dataSource.setJdbcUrl("jdbc:sqlite:" + sqliteFile);
        return SqliteStorage.dataSource.getConnection();
    }

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


    @Override
    public int get(final String aKey) {
        int returnValue = 0;
        try (final Connection connection = SqliteStorage.getConnection(this)) {
                final String tableName = this.configFile.getString(EnumConfig.SQLITE_TABLE);
                try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT query, SUM(count) as total FROM " + tableName + " WHERE query=? GROUP BY query ORDER BY total DESC\n")) {
                    preparedStatement.setString(1, aKey);
                    final ResultSet results = preparedStatement.executeQuery();
                    final InMemoryStorage storage = new InMemoryStorage();
                    while (results.next()) {
                        final String query = results.getString("query");
                        final int total = results.getInt("total");
                        storage.add(query, total);
                    }
                    results.close();
                    returnValue = storage.get(aKey);
                }

        } catch (final SQLException | PropertyVetoException ignored) {
        }
        return returnValue;
    }

    @Override
    public List<LogLine> elements() {
        final InMemoryStorage storage = new InMemoryStorage();
        try (final Connection connection = SqliteStorage.getConnection(this)) {
            try  (final PreparedStatement statement = connection.prepareStatement("SELECT query, SUM(count) as total FROM logs GROUP BY query ORDER BY total DESC")) {
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

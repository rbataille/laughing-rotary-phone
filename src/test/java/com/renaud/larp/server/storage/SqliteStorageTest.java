package com.renaud.larp.server.storage;

import com.renaud.larp.server.config.ConfigFile;
import com.renaud.larp.server.config.EnumConfig;
import org.junit.Assert;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SqliteStorageTest {


    @Test
    public void fullTest() throws IOException, SQLException, PropertyVetoException {
        final String filePath = ":memory:";
        final ConfigFile configFile = new ConfigFile();
        configFile.set(EnumConfig.SQLITE_FILE, "src/test/resources/data/test_application.sql");
        final SqliteStorage storage = new SqliteStorage(configFile);
        this.cleanDatabase(storage);

        Assert.assertEquals(0, storage.get("test"));
        Assert.assertEquals(0, storage.elements().size());
        storage.increment("test");

        Assert.assertEquals(1, storage.get("test"));
        storage.increment("test");
        Assert.assertEquals(2, storage.get("test"));

        for (int i = 0; i < 20; ++i) {
            storage.increment("newKey");
        }
        Assert.assertEquals(20, storage.get("newKey"));
        final Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }

    }

    private void cleanDatabase(final SqliteStorage storage) throws SQLException, PropertyVetoException {
        final Connection connection = SqliteStorage.getConnection(storage);
        assert connection != null;
        final Statement statement = connection.createStatement();
        statement.execute("DELETE FROM `logs`");
        statement.execute("VACUUM");
    }
}

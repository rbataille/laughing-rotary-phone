package com.renaud.larp;

import com.renaud.larp.server.config.ConfigFile;
import com.renaud.larp.server.config.EnumConfig;
import com.renaud.larp.server.storage.FileStorage;
import com.renaud.larp.server.storage.InMemoryStorage;
import com.renaud.larp.server.storage.SqliteStorage;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ApplicationTest {


    @Test
    public void storageModeTest(){
        final ConfigFile c1 = ConfigFile.empty();
        c1.set(EnumConfig.SERVER_PORT, "8020");
        c1.set(EnumConfig.STORAGE_TYPE, EnumConfig.EnumStorageType.SQLITE);
        c1.set(EnumConfig.STORAGE_FILE, ":memory:");
        final Application a1 = new Application(c1, null);
        Assert.assertEquals(SqliteStorage.class, a1.getStorage().getClass());

        final ConfigFile c2 = ConfigFile.empty();
        c2.set(EnumConfig.SERVER_PORT, "8020");
        c2.set(EnumConfig.STORAGE_TYPE, EnumConfig.EnumStorageType.FILE);
        c2.set(EnumConfig.STORAGE_FILE, "/tmp/storageFile");
        final Application a2 = new Application(c2, null);
        Assert.assertEquals(FileStorage.class, a2.getStorage().getClass());

        final ConfigFile inMemoryConfig = ConfigFile.empty();
        inMemoryConfig.set(EnumConfig.SERVER_PORT, "8020");
        inMemoryConfig.set(EnumConfig.STORAGE_TYPE, EnumConfig.EnumStorageType.MEMORY);
        final Application inMemoryApp = new Application(inMemoryConfig, null);
        Assert.assertEquals(InMemoryStorage.class, inMemoryApp.getStorage().getClass());
    }

    @Test
    public void multiApplicationTest() throws IOException, InterruptedException {
        final String filePath = "/tmp/ApplicationTest.multiApplicationTest";
        final Path path = Paths.get(filePath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
        final ConfigFile c1 = ConfigFile.empty();
        c1.set(EnumConfig.SERVER_PORT, "8021");
        c1.set(EnumConfig.STORAGE_FILE, filePath);
        final Application a1 = new Application(c1, null);
        a1.run();

        final ConfigFile c2 = ConfigFile.empty();
        c2.set(EnumConfig.SERVER_PORT, "8022");
        c2.set(EnumConfig.STORAGE_FILE, filePath);
        final Application a2 = new Application(c2, null);
        a2.run();

        final ConfigFile c3 = ConfigFile.empty();
        c3.set(EnumConfig.SERVER_PORT, "8023");
        c3.set(EnumConfig.STORAGE_FILE, filePath);
        final Application a3 = new Application(c3, null);
        a3.run();

        final String pushUri = "/app?int1=3&int2=5&limit=16&str1=str1Test1&str2=str2Test1";
        this.push(pushUri, 4, 8021);
        this.push(pushUri, 1, 8022);

        this.push("/app?int1=3&int2=5&limit=16&str1=A&str2=B", 6, 8023);
        this.push("/app?int1=3&int2=5&limit=16&str1=A&str2=B&start=10", 1, 8021);

        final String result1 = MainTest.get("localhost", "/metrics", 8021);
        final String result2 = MainTest.get("localhost", "/metrics", 8022);
        final String result3 = MainTest.get("localhost", "/metrics", 8023);

        Assert.assertEquals(result1, result2);
        Assert.assertEquals(result2, result3);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }



    @Test
    public void ParseCLITest() throws ParseException {
        final String configFile = "conf/config.local";
        final String[] parameters = {"-config", configFile};
        final CommandLine commandLine = Application.parseCommandLine(parameters);
        Assert.assertEquals(configFile, commandLine.getOptionValue("config", ""));
    }

    private void push(final String uri, final int count, final int port) throws IOException, InterruptedException {
        for (int i = 0; i < count; ++i) {
            MainTest.get("localhost", uri, port);
        }
        Thread.sleep(50);
    }
}

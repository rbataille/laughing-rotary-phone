package com.renaud.test.server.storage;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class FileStorageTest {


    @Test
    public void loadElementsFromFile(){
        final FileStorage storage = new FileStorage("src/test/resources/data/compacted_and_raw_data");
        Assert.assertEquals(4, storage.get("query1"));
        Assert.assertEquals(4, storage.get("query2"));
        Assert.assertEquals(21, storage.get("query3"));
        Assert.assertEquals(1, storage.get("query4"));
        Assert.assertEquals(1, storage.get("query5"));
        Assert.assertEquals(0, storage.get("query6"));
        Assert.assertEquals(0, storage.get("query7"));
        final List<LogLine> elements = storage.elements();
        for(final LogLine line: elements){
            if("query1".equals(line.getQuery())){
                Assert.assertEquals(4, line.getCount());
            } else if("query3".equals(line.getQuery())){
                Assert.assertEquals(21, line.getCount());
            }
        }
    }



    @Test
    public void writeElementTest() throws IOException {
        final Path filePath = Paths.get("/tmp/test_write_data_test");
        if(Files.exists(filePath)){
            Files.delete(filePath);
        }
        final FileStorage storage = new FileStorage(filePath.toString());
        Assert.assertEquals(0, storage.get("query1"));
        final int maxInsertion = 5;
        for(int i = 0; i < maxInsertion; ++i){
            storage.increment("query1");
        }
        Assert.assertEquals(maxInsertion, storage.get("query1"));
        if(Files.exists(filePath)){
            Files.delete(filePath);
        }

    }

}

package com.renaud.larp.handlers;

import com.google.gson.JsonArray;
import com.renaud.larp.server.http.JsonResponse;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.http.Response;
import com.renaud.larp.server.storage.InMemoryStorage;
import com.renaud.larp.server.storage.LogLine;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StatsHandlerTest {



    @Test
    public void handleTest() throws IOException {
        final InMemoryStorage storage = new InMemoryStorage();
        AppHandlerTest.setStorage(storage);
        for(int i = 0; i < 10; ++i){
            AppHandlerTest.responseFromInt1Int2Str1Str2("3", "5", "str1Test1", "str2Test1");
        }
        for(int i = 0; i < 7; ++i){
            AppHandlerTest.responseFromInt1Int2Str1Str2("4", "1", "str1Test2", "str2Test2");
        }
        AppHandlerTest.setStorage(new InMemoryStorage());


        final Request request = new Request();
        final StatsHandler handler = new StatsHandler(storage, "/stats");
        final Response r1 = handler.responseFromRequest(request);

        final byte[] fileContent = Files.readAllBytes(Paths.get("public/stats.html"));
        Assert.assertEquals(new String(fileContent, StandardCharsets.UTF_8), r1.getBody());

    }

}

package com.renaud.test.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.renaud.test.server.http.JsonResponse;
import com.renaud.test.server.http.Request;
import com.renaud.test.server.storage.InMemoryStorage;
import com.renaud.test.server.storage.LogLine;
import org.junit.Test;

public class MetricsHandlerTest {



    @Test
    public void handleTest() {
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
        final MetricsHandler handler = new MetricsHandler(storage, "/metrics");
        final JsonResponse r1 = (JsonResponse) handler.responseFromRequest(request);
        final JsonArray jsonLines = new JsonArray();;
        jsonLines.add((new LogLine("3;5;str1Test1;str2Test1", 10)).toJson());
        jsonLines.add((new LogLine("4;1;str1Test2;str2Test2", 7)).toJson());
        AppHandlerTest.checkSuccessResponse(r1, jsonLines);
    }

}

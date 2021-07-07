package com.renaud.larp.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.renaud.larp.server.http.JsonResponse;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.storage.AbstractStorage;
import com.renaud.larp.server.storage.InMemoryStorage;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class AppHandlerTest {
    private static final String LIMIT = "16";
    private static final String FIZZ = "fizz";
    private static final String BUZZ = "buzz";

    private static AbstractStorage STORAGE = new InMemoryStorage();

    public static void setStorage(final AbstractStorage storage){
        AppHandlerTest.STORAGE = storage;
    }

    public static JsonResponse responseFromInt1Int2Str1(final String int1, final String int2, final String str1) {
        return AppHandlerTest.responseFromInt1Int2Str1Str2(int1, int2, str1, BUZZ);
    }

    public static JsonResponse responseFromInt1Int2Str1Str2(final String int1, final String int2, final String str1, final String str2) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("int1", int1);
        parameters.put("int2", int2);
        parameters.put("limit", LIMIT);
        if (str1 != null) {
            parameters.put("str1", str1);
        }
        parameters.put("str2", str2);
        final Request request = new Request(parameters, "");


        Assert.assertEquals(parameters, request.getParameters());
        final AppHandler handler = new AppHandler(AppHandlerTest.STORAGE, "/app");
        return (JsonResponse) handler.responseFromRequest(request);
    }

    @Test
    public void testStartParameter() {
        final Map<String, String> parameters = new HashMap<>();
        parameters.put("int1", "30");
        parameters.put("int2", "10");
        parameters.put("limit", "30");
        parameters.put("start", "5");
        parameters.put("str1", FIZZ);
        parameters.put("str2", BUZZ);
        final Request request = new Request(parameters, "");
        Assert.assertEquals(parameters, request.getParameters());
        final AppHandler handler = new AppHandler(AppHandlerTest.STORAGE, "/app");
        final JsonResponse r1 = (JsonResponse) handler.responseFromRequest(request);
        Assert.assertEquals("success", r1.getObject().get("state").getAsString());
        final String result = r1.getObject().get("results").getAsString();
        final String expected = "5,6,7,8,9,buzz,11,12,13,14,15,16,17,18,19,buzz,21,22,23,24,25,26,27,28,29,fizzbuzz";
        Assert.assertEquals(expected, result);
    }

    public static JsonResponse responseFromInt1Int2(final String int1, final String int2) {
        return AppHandlerTest.responseFromInt1Int2Str1(int1, int2, FIZZ);
    }

    public static void checkSuccessResponse(final JsonResponse response, final JsonArray expected) {
        Assert.assertEquals("success", response.getObject().get("state").getAsString());
        final JsonArray results = response.getObject().get("results").getAsJsonArray();
        Assert.assertEquals(expected, results);
    }

    public static void checkErrorResponse(final JsonResponse response, final String aReason) {
        Assert.assertEquals("error", response.getObject().get("state").getAsString());
        Assert.assertEquals(aReason, response.getObject().get("reason").getAsString());
    }

    @Test
    public void handleTest() {
        final JsonResponse r1 = AppHandlerTest.responseFromInt1Int2("3", "5");
        final JsonArray e1 = new JsonArray();
        e1.add(new JsonPrimitive("1,2,fizz,4,buzz,fizz,7,8,fizz,buzz,11,fizz,13,14,fizzbuzz,16"));;
        AppHandlerTest.checkSuccessResponse(r1, e1);

        final JsonResponse r2 = AppHandlerTest.responseFromInt1Int2("4", "8");
        final JsonArray e2 = new JsonArray();
        e2.add(new JsonPrimitive("1,2,3,fizz,5,6,7,fizzbuzz,9,10,11,fizz,13,14,15,fizzbuzz"));;
        AppHandlerTest.checkSuccessResponse(r2, e2);
    }

    @Test
    public void illegalArgumentExceptionTest() {
        final JsonResponse r1 = AppHandlerTest.responseFromInt1Int2("invalidint", "5");
        final String e1 = "Parameter 'int1' need to be an int";
        AppHandlerTest.checkErrorResponse(r1, e1);

        final JsonResponse r2 = AppHandlerTest.responseFromInt1Int2("3", "invalidint");
        final String e2 = "Parameter 'int2' need to be an int";
        AppHandlerTest.checkErrorResponse(r2, e2);

        final JsonResponse r3 = AppHandlerTest.responseFromInt1Int2Str1("3", "5", null);
        final String e3 = "The parameter 'str1' is missing : " + AppHandler.HELP_DESCRIPTION;
        AppHandlerTest.checkErrorResponse(r3, e3);
    }
}

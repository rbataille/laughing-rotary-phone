package com.renaud.test.server.http;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class JsonResponse extends Response {
    private final JsonObject object;

    public JsonResponse(final JsonObject aJsonObject) {
        this(aJsonObject, 200);
    }

    public JsonResponse(final JsonObject aJsonObject, final int code) {
        super(aJsonObject.toString(), code);
        this.object = aJsonObject;
        this.addHeader("Content-Type", "application/json");
    }

    /**
     * Basic error response.
     *
     * @param anErrorMessage The message of the error.
     * @return the JsonResponse.
     */
    public static JsonResponse error(final String anErrorMessage, final int code) {
        final JsonObject jsonResponse = new JsonObject();
        jsonResponse.add("state", new JsonPrimitive("error"));
        jsonResponse.add("reason", new JsonPrimitive(anErrorMessage));
        return new JsonResponse(jsonResponse, code);
    }

    public JsonObject getObject() {
        return this.object;
    }
}

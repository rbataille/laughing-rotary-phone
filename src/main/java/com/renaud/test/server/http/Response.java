package com.renaud.test.server.http;

import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.Map;

public class Response {

    private final String body;
    private final int code;
    private final Map<String, String> headers = new HashMap<>();

    public Response(final String aResponseBody, final int aCode) {
        this.body = aResponseBody;
        this.code = aCode;
        this.addHeader("Content-Type", "text/html");
    }

    public void addHeader(final String aKey, final String aValue) {
        this.headers.put(aKey, aValue);
    }

    public void flush(final HttpExchange anHttpExchange) {
        for (final Map.Entry<String, String> entry : this.headers.entrySet()) {
            anHttpExchange.getResponseHeaders().set(entry.getKey(), entry.getValue());
        }
        try {
            anHttpExchange.sendResponseHeaders(this.code, this.body.getBytes().length);
            anHttpExchange.getResponseBody().write(this.body.getBytes());
        } catch (final Exception ignored) {
        }
        anHttpExchange.close();

    }
}

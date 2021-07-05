package com.renaud.test.server.http;

import java.util.HashMap;
import java.util.Map;

public class Request {

    private final Map<String, String> parameters = new HashMap<>();

    public Request(final Map<String, String> parameters) {
        this.parameters.putAll(parameters);
    }

    public Request() {
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }
}

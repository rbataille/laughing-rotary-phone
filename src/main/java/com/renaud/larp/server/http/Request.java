package com.renaud.larp.server.http;

import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Request {

    private final Map<String, String> parameters = new HashMap<>();
    private final String uri;

    public static Request fromHttpExchange(final HttpExchange exchange) {
        final Map<String,String> parameters = Request.extractParametersFromExchange(exchange);
        return new Request(parameters, exchange.getRequestURI().getPath());
    }

    public String getUri(){
        return this.uri;
    }

    public Request(final Map<String, String> parameters, final String uri) {
        this.uri = uri;
        this.parameters.putAll(parameters);
    }

    public Request() {
        this.uri = "";
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }




    /**
     * Extract parameters from request body.
     *
     * @param anHttpExchange The http exchange.
     */
    private static Map<String, String> extractPostParametersFromExchange(final HttpExchange anHttpExchange) {
        final Map<String, String> output = new HashMap<>();
        if (!"post".equalsIgnoreCase(anHttpExchange.getRequestMethod())) {
            return output;
        }
        final InputStream is = anHttpExchange.getRequestBody();
        // Read whole request body
        final Scanner s = new Scanner(is).useDelimiter("\\A");
        final String query = s.hasNext() ? s.next() : "";
        if (query.isEmpty()) {
            return output;
        }
        final Map<String, String> parameters = Request.extractParametersFromQuery(query);
        output.putAll(parameters);
        return output;
    }

    /**
     * @param anHttpExchange incoming exchange
     * @return List of all GET or POST parameters.
     */
    public static Map<String, String> extractParametersFromExchange(final HttpExchange anHttpExchange) {
        final Map<String, String> parameters = new HashMap<>(Request.extractPostParametersFromExchange(anHttpExchange));
        final URI uri = anHttpExchange.getRequestURI();
        final String query = uri.getQuery();

        parameters.putAll(Request.extractParametersFromQuery(query));
        return parameters;
    }


    /**
     * Extract parameters from the url (/index.php?test=foo&bar=baz)
     * @param query
     * @return
     */
    private static Map<String, String> extractParametersFromQuery(final String query) {
        final Map<String, String> output = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return output;
        }
        String[] queryParts = new String[]{query};
        if (query.contains("&")) {
            queryParts = query.split("&");
        }
        for (final String part : queryParts) {
            final String[] vars = part.split("=");
            if (vars.length == 2) {
                output.put(vars[0], vars[1]);
            }
        }
        return output;
    }


}

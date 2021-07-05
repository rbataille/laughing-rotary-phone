package com.renaud.test.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.renaud.test.server.http.JsonResponse;
import com.renaud.test.server.http.Request;
import com.renaud.test.server.http.Response;
import com.renaud.test.server.storage.IStorage;
import com.sun.net.httpserver.HttpExchange;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * The AbstractHandler provide the abstract function handle.
 *
 * @see #responseFromRequest(Request)
 */
public abstract class AbstractHandler {
    /**
     * Route used to reach this handler.
     */
    protected final String route;

    /**
     * Storage
     */
    protected final IStorage storage;

    /**
     * Basic constructor.
     *
     * @param aRouteStr The route string.
     * @param storage   The storage object.
     */
    protected AbstractHandler(final IStorage storage, final String aRouteStr) {
        this.route = aRouteStr;
        this.storage = storage;
    }

    /**
     * Return true if the current uri match the route pattern.
     *
     * @param anUri The uri.
     * @return true if match, false otherwise.
     */
    public boolean isRouteMatchingUri(final String anUri) {
        return this.route.matches(anUri);
    }

    /**
     * Called when a client ask for the
     *
     * @param request@return a Response object.
     */
    public abstract Response responseFromRequest(final Request request);


    /**
     * Return a success response as a json object.
     *
     * @param aPropertyName The name of the property.
     * @param aJsonElement  The Json element to add.
     * @return JsonObject {'state':'success',aPropertyName: aJsonElement}
     */
    protected JsonResponse success(final String aPropertyName, final JsonElement aJsonElement) {
        final JsonObject object = new JsonObject();
        object.add("state", new JsonPrimitive("success"));
        object.add(aPropertyName, aJsonElement);
        return new JsonResponse(object);
    }

    /**
     * Return an error response as json.
     *
     * @param aReason The reason of the error response.
     * @return JsonObject {'state':'error','reason': aReason}
     */
    protected JsonResponse error(final String aReason) {
        final JsonObject object = new JsonObject();
        object.add("state", new JsonPrimitive("error"));
        object.add("reason", new JsonPrimitive(aReason));
        return new JsonResponse(object);
    }

    /**
     * Extract parameters from request body.
     *
     * @param anHttpExchange The http exchange.
     */
    private Map<String, String> extractPostParametersFromExchange(final HttpExchange anHttpExchange) {
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

        final Map<String, String> parameters = this.extractParametersFromQuery(query);
        output.putAll(parameters);
        return output;
    }

    private Map<String, String> extractParametersFromQuery(final String query) {
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

    /**
     * @param anHttpExchange incoming exchange
     * @return List of all GET or POST parameters.
     */
    public Map<String, String> extractParametersFromExchange(final HttpExchange anHttpExchange) {
        final Map<String, String> parameters = new HashMap<>(this.extractPostParametersFromExchange(anHttpExchange));
        final URI uri = anHttpExchange.getRequestURI();
        final String query = uri.getQuery();

        parameters.putAll(this.extractParametersFromQuery(query));
        return parameters;
    }
}

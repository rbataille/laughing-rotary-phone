package com.renaud.larp.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.renaud.larp.server.http.JsonResponse;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.http.Response;
import com.renaud.larp.server.storage.AbstractStorage;

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
    protected final AbstractStorage storage;

    /**
     * Basic constructor.
     *
     * @param aRouteStr The route string.
     * @param storage   The storage object.
     */
    protected AbstractHandler(final AbstractStorage storage, final String aRouteStr) {
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
     * Called when a client ask for something.
     * We convert the client's request to the server response.
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

}

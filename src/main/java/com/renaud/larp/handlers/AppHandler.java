package com.renaud.larp.handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import com.renaud.larp.server.AbstractHandler;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.http.Response;
import com.renaud.larp.server.storage.AbstractStorage;

import java.util.Map;
import java.util.StringJoiner;

public class AppHandler extends AbstractHandler {

    static final String HELP_DESCRIPTION = "Returns a list of strings with numbers from 1 to limit, where: all multiples of int1 are replaced by str1, all multiples of int2\n" +
            " are replaced by str2, all multiples of int1 and int2 are replaced by str1str2.";

    /**
     * Basic constructor.
     *
     * @param storage   The storage used.
     * @param aRouteStr Route used to reach this handler.
     */
    public AppHandler(final AbstractStorage storage, final String aRouteStr) {
        super(storage, aRouteStr);
    }

    /**
     * Handle an http request on the server.
     *
     * @see AppHandler#run(Request)
     * @param request An incoming HttpRequest.
     * @return a Response object.
     */
    @Override
    public Response responseFromRequest(final Request request) {
        try {
            return this.run(request);
        } catch (final IllegalArgumentException e) {
            return this.error(e.getMessage());
        }

    }

    /**
     * Extract 5 parameters from the request:
     * int1, int2, limit ( integers ), str1, str2 (need to be of type string)
     * we have the start optional parameter (need to be of type int)
     *
     * then we generate an array of numbers from start (or 1) to limit.
     * After that we fizzbuzz our list using str1 and str2.
     *
     * @param request The incoming request, need to have 5 parameters int1, int2, limit, str1, and str2.
     * @return The server response as json.
     * @throws IllegalArgumentException This exception rise when one or more parameters are missing or when int1,int2,limit are not numeric.
     */
    public Response run(final Request request) throws IllegalArgumentException {
        final Map<String, String> parameters = request.getParameters();
        final StringJoiner queryBuilder = new StringJoiner(";");
        final int int1 = this.getIntParameter(parameters, "int1", queryBuilder);
        final int int2 = this.getIntParameter(parameters, "int2", queryBuilder);
        final int limit = this.getIntParameter(parameters, "limit", null);
        // Parameter start or 1
        final int start = this.getStartParameter(parameters);
        final String str1 = this.getStringParameter(parameters, "str1", queryBuilder);
        final String str2 = this.getStringParameter(parameters, "str2", queryBuilder);

        final StringJoiner joiner = new StringJoiner(",");
        for (int i = start; i <= limit; ++i) {
            final boolean isModuloInt1 = (i % int1 == 0);
            final boolean isModuloInt2 = (i % int2 == 0);
            if (isModuloInt1 && isModuloInt2) {
                joiner.add(str1 + str2);
            } else if (isModuloInt1) {
                joiner.add(str1);
            } else if (isModuloInt2) {
                joiner.add(str2);
            } else {
                joiner.add(Integer.toString(i));
            }
        }
        // Log the query in our backend.
        this.storage.increment(queryBuilder.toString());
        final JsonArray results = new JsonArray();
        results.add(new JsonPrimitive(joiner.toString()));
        return this.success("results", results);
    }


    /**
     * Return parameters[aKey] as int or aDefaultValue if something goes wrong.
     *
     * @param parameters List of parameters
     * @return An integer.
     */
    private int getStartParameter(final Map<String, String> parameters){
        try {
            return this.getIntParameter(parameters, "start", null);
        } catch(final IllegalArgumentException e){
            return 1;
        }
    }

    /**
     * Return an int from the aParameters map.
     *
     * @param aParameters List of parameters
     * @param aKey The key to search for.
     * @return int
     * @throws IllegalArgumentException If aParameters doesnt contain aKey, or if we catch aNumberFormatException
     */
    private int getIntParameter(final Map<String, String> aParameters, final String aKey, final StringJoiner queryBuilder) throws IllegalArgumentException {
        try {
            final int intValue = Integer.parseInt(this.getStringParameter(aParameters, aKey, null));
            if(queryBuilder != null) {
                queryBuilder.add(Integer.toString(intValue));
            }
            return intValue;
        } catch (final NumberFormatException e) {
            throw new IllegalArgumentException("Parameter '" + aKey + "' need to be an int");
        }
    }

    /**
     * Return parameters[aKey].
     * @param parameters The list of parameter.
     * @param aKey The key
     * @param queryBuilder The StringJoiner use to build the query that while be log in the storage
     * @return
     * @throws IllegalArgumentException
     */
    private String getStringParameter(final Map<String, String> parameters, final String aKey, final StringJoiner queryBuilder) throws IllegalArgumentException {
        if (!parameters.containsKey(aKey)) {
            throw new IllegalArgumentException("The parameter '" + aKey + "' is missing : " + HELP_DESCRIPTION);
        }
        final String value = parameters.get(aKey);
        if (queryBuilder != null) {
            queryBuilder.add(value);
        }
        return value;
    }
}

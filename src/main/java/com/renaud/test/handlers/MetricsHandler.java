package com.renaud.test.handlers;

import com.google.gson.JsonArray;
import com.renaud.test.server.AbstractHandler;
import com.renaud.test.server.http.Request;
import com.renaud.test.server.http.Response;
import com.renaud.test.server.storage.IStorage;
import com.renaud.test.server.storage.LogLine;

import java.util.Collections;
import java.util.List;

public class MetricsHandler extends AbstractHandler {
    /**
     * Basic constructor.
     *
     * @param aRouteStr The route string.
     */
    public MetricsHandler(final IStorage storage, final String aRouteStr) {
        super(storage, aRouteStr);
    }

    /**
     * @param counters
     * @return
     */
    private static JsonArray countersToJsonArray(final List<LogLine> counters) {
        final JsonArray lines = new JsonArray();
        Collections.sort(counters);
        for (final LogLine queryInfo : counters) {
            lines.add(queryInfo.toJson());
        }
        return lines;
    }

    @Override
    public Response responseFromRequest(final Request request) {
        final List<LogLine> counters = this.storage.elements();
        final JsonArray response = MetricsHandler.countersToJsonArray(counters);
        return this.success("results", response);
    }

}

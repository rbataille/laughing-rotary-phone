package com.renaud.larp.handlers;

import com.google.gson.JsonArray;
import com.renaud.larp.server.AbstractHandler;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.http.Response;
import com.renaud.larp.server.storage.AbstractStorage;
import com.renaud.larp.server.storage.LogLine;

import java.util.Collections;
import java.util.List;

public class MetricsHandler extends AbstractHandler {
    /**
     * Basic constructor.
     *
     * @param aRouteStr The route string.
     */
    public MetricsHandler(final AbstractStorage storage, final String aRouteStr) {
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

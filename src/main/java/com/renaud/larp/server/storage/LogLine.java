package com.renaud.larp.server.storage;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

/**
 * Piece of information extracted from a random backend
 */
public class LogLine implements Comparable<LogLine> {
    /**
     * Counter for this query.
     */
    private final int count;
    /**
     * The query as string
     */
    private final String query;

    /**
     * Build a LogLine by yourself!
     * @param query A string.
     * @param count The count.
     */
    public LogLine(final String query, final int count){
        this.query = query;
        this.count = count;
    }

    /**
     * Convert our piece of informations into a json object.
     * @return JsonObject with query,count properties.
     */
    public JsonObject toJson(){
        final JsonObject entry = new JsonObject();
        entry.add("query", new JsonPrimitive(this.getQuery()));
        entry.add("count", new JsonPrimitive(this.getCount()));
        return entry;
    }

    /**
     * Accessor
     * @return The query
     */
    public String getQuery(){
        return this.query;
    }
    public int getCount(){
        return this.count;
    }

    /**
     * Used so our line can be sorted by the count property.
     * @param logLine Logline to compare to.
     * @return int < 0 if lesser, 0 equals, > 0 greater.
     */
    @Override
    public int compareTo(final LogLine logLine) {
        return logLine.count - this.count;
    }
}

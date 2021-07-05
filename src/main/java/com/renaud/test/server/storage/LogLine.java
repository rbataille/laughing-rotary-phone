package com.renaud.test.server.storage;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class LogLine implements Comparable<LogLine> {
    private final int count;
    private final String query;

    public LogLine(final String query, final int count){
        this.query = query;
        this.count = count;
    }

    public JsonObject toJson(){
        final JsonObject entry = new JsonObject();
        entry.add("query", new JsonPrimitive(this.getQuery()));
        entry.add("count", new JsonPrimitive(this.getCount()));
        return entry;
    }

    public String getQuery(){
        return this.query;
    }

    public int getCount(){
        return this.count;
    }

    @Override
    public int compareTo(final LogLine logLine) {
        return logLine.count - this.count;
    }

    @Override
    public boolean equals(final Object logLine) {
        return super.equals(logLine);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

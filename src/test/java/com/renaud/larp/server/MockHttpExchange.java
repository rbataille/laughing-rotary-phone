package com.renaud.larp.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public class MockHttpExchange extends HttpExchange {
    private final ByteArrayOutputStream baos;
    private final URI uri;
    private int code;

    public MockHttpExchange(final String anUri) throws URISyntaxException {
        this.uri = new URI(anUri);
        this.baos = new ByteArrayOutputStream();
    }

    @Override
    public Headers getRequestHeaders() {
        return null;
    }

    @Override
    public Headers getResponseHeaders() {
        return new Headers();
    }

    @Override
    public URI getRequestURI() {
        return this.uri;
    }

    @Override
    public String getRequestMethod() {
        return null;
    }

    @Override
    public HttpContext getHttpContext() {
        return null;
    }

    @Override
    public void close() {

    }

    @Override
    public InputStream getRequestBody() {
        return null;
    }

    @Override
    public OutputStream getResponseBody() {
        return this.baos;
    }

    @Override
    public void sendResponseHeaders(int i, long l) throws IOException {
        this.code = i;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return new InetSocketAddress("127.0.0.1", 80);
    }

    @Override
    public int getResponseCode() {
        return this.code;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return null;
    }

    @Override
    public void setAttribute(String s, Object o) {

    }

    @Override
    public void setStreams(InputStream inputStream, OutputStream outputStream) {

    }

    @Override
    public HttpPrincipal getPrincipal() {
        return null;
    }

    public String getResponseBodyAsString() {
        return this.baos.toString();
    }
}

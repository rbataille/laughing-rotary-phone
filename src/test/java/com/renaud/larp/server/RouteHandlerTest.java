package com.renaud.larp.server;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

public class RouteHandlerTest {

    @Test
    public void handleTest() throws URISyntaxException {
        final RouteHandler handler = new RouteHandler();

        final MockHttpExchange exchange = new MockHttpExchange("/route_a");
        handler.handle(exchange);
        Assert.assertEquals(404, exchange.getResponseCode());
        Assert.assertEquals("{\"state\":\"error\",\"reason\":\"Page not found\"}", exchange.getResponseBodyAsString());
    }

}

package com.renaud.larp.server;

import com.renaud.larp.server.http.JsonResponse;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.http.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.util.*;

/**
 * Some helpers to handle Http traffic
 */
public class RouteHandler implements HttpHandler {

    private final List<AbstractHandler> appHandlers = new ArrayList<>();

    public void registerHandler(final AbstractHandler aController) {
        this.appHandlers.add(aController);
    }

    @Override
    public void handle(final HttpExchange anHttpExchange) {
        for (final AbstractHandler controller : this.appHandlers) {
            final Request request = Request.fromHttpExchange(anHttpExchange);
            if (!controller.isRouteMatchingUri(request.getUri())) {
                continue;
            }
            final Response controllerResponse = controller.responseFromRequest(request);
            controllerResponse.flush(anHttpExchange);
            return;
        }
        final JsonResponse response = JsonResponse.error("Page not found", 404);
        response.flush(anHttpExchange);
    }
}

package com.renaud.test.server;

import com.renaud.test.server.http.JsonResponse;
import com.renaud.test.server.http.Request;
import com.renaud.test.server.http.Response;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.net.URI;
import java.util.*;

public class RouteHandler implements HttpHandler {

    private final List<AbstractHandler> controllers = new ArrayList<>();

    public void registerController(final AbstractHandler aController) {
        this.controllers.add(aController);
    }

    @Override
    public void handle(final HttpExchange anHttpExchange) {
        for (final AbstractHandler controller : this.controllers) {
            final String trueUri = this.extractUri(anHttpExchange.getRequestURI());
            if (!controller.isRouteMatchingUri(trueUri)) {
                continue;
            }
            final Map<String, String> parameters = controller.extractParametersFromExchange(anHttpExchange);
            final Request request = new Request(parameters);
            final Response controllerResponse = controller.responseFromRequest(request);
            controllerResponse.flush(anHttpExchange);
            return;
        }
        final JsonResponse response = JsonResponse.error("Page not found", 404);
        response.flush(anHttpExchange);
    }


    private String extractUri(final URI aRequestUri) {
        return aRequestUri.getPath();
    }
}

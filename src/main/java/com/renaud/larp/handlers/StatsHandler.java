package com.renaud.larp.handlers;

import com.renaud.larp.server.AbstractHandler;
import com.renaud.larp.server.http.JsonResponse;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.http.Response;
import com.renaud.larp.server.storage.AbstractStorage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class StatsHandler extends AbstractHandler {

    /**
     * Basic constructor.
     *
     * @param storage   The storage used.
     * @param aRouteStr Route used to reach this handler.
     */
    public StatsHandler(final AbstractStorage storage, final String aRouteStr) {
        super(storage, aRouteStr);
    }

    /**
     * Handle a request on the endpoint /stats.
     * Return the file  located at public/stats.html
     *
     * @param request An incoming HttpRequest.
     * @return a Response object as HTML if the file exists, as JSON if the file doesnt exists.
     */
    @Override
    public Response responseFromRequest(final Request request) {
        final String filePath = "public/stats.html";
        try {
            final byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            final Response response = new Response(new String(fileBytes, StandardCharsets.UTF_8), 200);
            response.addHeader("Content-Type", "text/html");
            return response;
        } catch (IOException e) {
            return JsonResponse.error(e.getMessage(), 500);
        }
    }
}

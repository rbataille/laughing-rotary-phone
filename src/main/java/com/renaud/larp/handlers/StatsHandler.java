package com.renaud.larp.handlers;

import com.renaud.larp.server.AbstractHandler;
import com.renaud.larp.server.http.JsonResponse;
import com.renaud.larp.server.http.Request;
import com.renaud.larp.server.http.Response;
import com.renaud.larp.server.storage.AbstractStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StatsHandler extends AbstractHandler {
    private static final Logger LOG = LoggerFactory.getLogger(StatsHandler.class);

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
     * Handle an http request on the server.
     *
     * @param request An incoming HttpRequest.
     * @return a Response object.
     */
    @Override
    public Response responseFromRequest(final Request request) {
        final Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        LOG.info("Current absolute path is: " + s);

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

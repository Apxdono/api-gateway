package org.example.api.gw.proxy;

import io.javalin.http.Context;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.example.api.gw.config.Config;
import org.example.api.gw.config.gateway.Endpoint;
import org.example.api.gw.config.gateway.Route;
import org.example.api.gw.contexts.RouteContext;
import org.example.api.gw.exceptions.EndpointException;
import org.example.api.gw.utils.ContentTypeUtils;
import org.example.api.gw.utils.ContextRequestBody;
import org.example.api.gw.utils.HttpUrlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Wraps internals of incoming request processing creating valid {@link RouteContext} (or failing miserably).
 * This context will be used further in the pipeline in order to send request
 * to underlying {@link Endpoint}.
 */
public class Router {
    private static final Logger LOG = LoggerFactory.getLogger(Router.class);
    private static final Supplier<RequestBody> NO_BODY_SUPPLIER = () -> RequestBody.create(new byte[0], null);
    private static final Set<String> NO_BODY_METHODS = new HashSet<>(Arrays.asList("GET", "HEAD"));

    private final Config config;
    private final Route route;

    public Router(Config config, Route route) {
        this.config = config;
        this.route = route;
    }

    public RouteContext buildRoute(Context ctx) {
        var target = route.target();

        var endpoint = target.evaluateEndpoint(ctx, config::findEndpoint)
                .orElseThrow(() -> {
                    LOG.error("No suitable endpoint for route '{}', path '{}'", route.name(), ctx.path());
                    return new EndpointException("Cannot locate requested resource " + ctx.path());
                });

        var url = HttpUrlHelper.mergeWithPath(endpoint.url(), () -> target.evaluatePath(ctx), target.pathMode());

        var contentType = ContentTypeUtils.toMediaTypeSafe(ctx.contentType());
        var body = prepareBodyExtractor(ctx, contentType);

        var routeCtx = RouteContext.create()
                .withMethod(ctx.method())
                .withRoute(route)
                .withEndpoint(endpoint)
                .withBody(body)
                .withEndpointUrl(HttpUrlHelper.copyQueryParams(ctx, url))
                .withContentType(contentType)
                .build();

        return routeCtx;
    }

    private static Supplier<RequestBody> prepareBodyExtractor(Context ctx, MediaType contentType) {
        if (NO_BODY_METHODS.contains(ctx.method())) {
            return () -> null;
        }

        if (hasContent(ctx) || ctx.isMultipartFormData()) {
            return () -> new ContextRequestBody(ctx, contentType);
        }

        return NO_BODY_SUPPLIER;
    }

    private static boolean hasContent(Context ctx) {
        return ctx.contentLength() > 0;
    }
}

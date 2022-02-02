package org.example.api.gw.handlers;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import org.example.api.gw.Entrypoint;
import org.example.api.gw.config.Config;
import org.example.api.gw.config.gateway.Route;
import org.example.api.gw.config.gateway.RouteTarget;
import org.example.api.gw.proxy.Invoker;
import org.example.api.gw.proxy.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.javalin.apibuilder.ApiBuilder.before;
import static io.javalin.apibuilder.ApiBuilder.path;

/**
 * Registers endpoints based on {@link Config#routes()}.
 */
public class RouteRegistration {
    private static final Logger LOG = LoggerFactory.getLogger(RouteRegistration.class);

    public static void setupRoutes(Config config, Javalin app, Invoker invoker) {
        config.routes().forEach(route -> {
            var routeUrl = route.routePath();
            var routeHandler = initHandler(config, route, invoker);

            app.routes(() -> {
                before(routeUrl, Handlers.correlationIdHandler());
                before(routeUrl, Entrypoint.multipartHandler());
                before(routeUrl, Handlers.forwardInfoHandler());
                path(routeUrl, () -> route.allowedMethods().forEach(method -> method.handleMethod(routeHandler)));
                LOG.info("Registered {} route {} for {} with methods {}",
                        route.name(), routeUrl, route.target(), route.allowedMethods());
            }) ;
        });
    }

    private static Handler initHandler(Config config, Route route, Invoker invoker) {
        if (route.target().type() == RouteTarget.Type.MAPPED) {
            return new MappedEndpointHandler(config, new Router(config, route), invoker);
        }
        return ctx -> {};
    }
}

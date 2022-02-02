package org.example.api.gw.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.api.gw.config.Config;
import org.example.api.gw.proxy.Invoker;
import org.example.api.gw.proxy.Router;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MappedEndpointHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(MappedEndpointHandler.class);

    private final Config config;
    private final Router router;
    private final Invoker invoker;

    public MappedEndpointHandler(@NotNull Config config, @NotNull Router router, Invoker invoker) {
        this.config = config;
        this.router = router;
        this.invoker = invoker;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        var routeCtx = router.buildRoute(ctx);
        invoker.invokeRequest(ctx, routeCtx);
    }
}

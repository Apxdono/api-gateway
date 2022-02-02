package org.example.api.gw.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.api.gw.contexts.ProxyContext;
import org.jetbrains.annotations.NotNull;

class ForwardDetailsHandler implements Handler {
    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ProxyContext.using(ctx).addProxyHeader("X-Forwarded-For", ctx.ip())
                .addProxyHeader("X-Forwarded-Host", ctx.host())
                .addProxyHeader("X-Forwarded-Method", ctx.method());
    }
}

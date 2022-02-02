package org.example.api.gw.handlers;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.example.api.gw.contexts.ProxyContext;
import org.example.api.gw.utils.Strings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

class CorrelationIdHandler implements Handler {
    private static final Logger LOG = LoggerFactory.getLogger(CorrelationIdHandler.class);

    private static final String HEADER_NAME = "Correlation-Id";
    private static final String LOGGING_NAME = "correlation-id";

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var correlationId = ctx.header(HEADER_NAME);
        if (Strings.isAbsent(correlationId)) {
            correlationId = UUID.randomUUID().toString();
            LOG.info("Created new correlation id {}", correlationId);
        }

        ProxyContext
                .using(ctx)
                .addProxyHeader(HEADER_NAME, correlationId)
                .addLoggingField(LOGGING_NAME, correlationId);

        ctx.header(HEADER_NAME, correlationId);
        LOG.info("Using header value: '{}'", correlationId);
    }
}

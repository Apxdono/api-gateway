package org.example.api.gw.handlers;


import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.jetbrains.annotations.NotNull;

import java.io.StringWriter;

public class PrometheusMetricsHandler implements Handler {
    private final CollectorRegistry registry;

    public PrometheusMetricsHandler(CollectorRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        var contentType = TextFormat.chooseContentType(ctx.contentType());
        var sw = new StringWriter();
        TextFormat.writeFormat(contentType, sw, registry.metricFamilySamples());
        ctx.contentType(contentType).result(sw.getBuffer().toString());
    }
}

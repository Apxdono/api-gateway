package org.example.api.gw;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;
import io.javalin.core.compression.CompressionStrategy;
import io.javalin.core.util.RouteOverviewPlugin;
import io.javalin.http.ContentType;
import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJackson;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.jetty.JettyStatisticsCollector;
import io.prometheus.client.jetty.QueuedThreadPoolStatisticsCollector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.example.api.gw.config.Config;
import org.example.api.gw.handlers.PrometheusMetricsHandler;
import org.example.api.gw.handlers.RouteRegistration;
import org.example.api.gw.proxy.EndpointClients;
import org.example.api.gw.proxy.Invoker;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.MultipartConfigElement;
import java.util.function.Consumer;

public class Entrypoint {
    private static final Logger LOG = LoggerFactory.getLogger(Entrypoint.class);

    public static void runServer(ObjectMapper mapper, Config config) {
        var app = Javalin.create(configureJavalin(mapper));

        var metrics = CollectorRegistry.defaultRegistry;
        serverMetrics(metrics, app.jettyServer().server());
        app.get("/admin/metrics", new PrometheusMetricsHandler(metrics));

        var endpointClients = new EndpointClients(config);
        var invoker = new Invoker(endpointClients);
        RouteRegistration.setupRoutes(config, app, invoker);

        app.start(config.global().port());
    }

    @NotNull
    private static Consumer<JavalinConfig> configureJavalin(ObjectMapper mapper) {
        return javalinConf -> {
            javalinConf.showJavalinBanner = false;
            javalinConf.defaultContentType = ContentType.JSON;
            javalinConf.jsonMapper(new JavalinJackson(mapper));
            javalinConf.registerPlugin(new RouteOverviewPlugin("/routes"));
            javalinConf.compressionStrategy(CompressionStrategy.NONE);
            javalinConf.server(() -> {
                var workers = new QueuedThreadPool(200, 20, 60_000);
                return new Server(workers);
            });
        };
    }

    private static void serverMetrics(CollectorRegistry registry, Server server) {
        var stats = new StatisticsHandler();
        registry.register(new JettyStatisticsCollector(stats));
        registry.register(new QueuedThreadPoolStatisticsCollector()
                .add((QueuedThreadPool) server.getThreadPool(), "gateway-pool"));
        server.setHandler(stats);
    }



    public static Handler multipartHandler() {
        final var tempFolder = System.getProperty("java.io.tmpdir");
        final var maxPartSize = 2; //5M
        final var maxRequestSize = 2 * maxPartSize; //25M
        final var inMemThresholdSize = 2;
        final var multipartConfig = new MultipartConfigElement(tempFolder, maxPartSize, maxRequestSize, inMemThresholdSize);

        return ctx -> {
            ctx.attribute(Request.MULTIPART_CONFIG_ELEMENT, multipartConfig);
            LOG.debug("Multipart limitations applied");
        };
    }
}

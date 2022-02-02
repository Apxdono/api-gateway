package org.example.api.gw.proxy;

import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.example.api.gw.config.Config;
import org.example.api.gw.config.gateway.Endpoint;
import org.example.api.gw.config.http.HttpConfig;
import org.example.api.gw.config.http.RequestLimitConfig;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Performs setup and lookup of individual {@link Endpoint} {@link OkHttpClient}s.
 */
public class EndpointClients {

    private final OkHttpClient globalClient;
    private final Map<Endpoint, OkHttpClient> endpointClients;

    public EndpointClients(Config config) {
        this.globalClient = setupClient(config.global().httpConfig(), null);
        this.endpointClients = setupEndpointClients(config);
    }

    public OkHttpClient getClient(Endpoint endpoint) {
        return endpointClients.get(endpoint);
    }

    private Map<Endpoint, OkHttpClient> setupEndpointClients(Config config) {
        return config.endpoints().stream().collect(Collectors.toMap(e -> e, e ->setupClient(e.httpConfig(), globalClient)));
    }

    private static OkHttpClient setupClient(HttpConfig httpConfig, OkHttpClient parent) {
        var client = (parent != null ? parent.newBuilder() : new OkHttpClient.Builder());

        if (httpConfig != null) {
            client.connectionPool(createPool(httpConfig))
                    .followRedirects(httpConfig.followRedirects())
                    .followSslRedirects(httpConfig.followRedirects());

            if (httpConfig.requests() != null) {
                client.dispatcher(createDispatcher(httpConfig.requests()));
            }

            var timeout = httpConfig.timeout();

            if (timeout != null) {
                client.connectTimeout(timeout.connect())
                        .readTimeout(timeout.read())
                        .callTimeout(timeout.cappedAt());
            }
        }
        return client.build();
    }

    private static ConnectionPool createPool(HttpConfig config) {
        return new ConnectionPool(config.maxIdle(), config.keepAliveDuration().toSeconds(), TimeUnit.SECONDS);
    }

    private static Dispatcher createDispatcher(RequestLimitConfig requestLimits) {
        var dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(requestLimits.max());
        dispatcher.setMaxRequestsPerHost(requestLimits.maxPerHost());
        return dispatcher;
    }

}

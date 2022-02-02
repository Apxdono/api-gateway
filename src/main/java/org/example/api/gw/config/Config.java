package org.example.api.gw.config;

import org.example.api.gw.config.gateway.Endpoint;
import org.example.api.gw.config.gateway.Route;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Config {
    private ConfigDelegate delegate;

    public static Config prepare() {
        return new Config();
    }

    public GlobalConfig global() {
        return delegate.global();
    }

    public List<Endpoint> endpoints() {
        return delegate.endpoints();
    }

    public Map<String, Endpoint> endpointMap() {
        return delegate.endpointMap();
    }

    public Optional<Endpoint> findEndpoint(String name) {
        return Optional.ofNullable(delegate.endpointMap().get(name));
    }

    public List<Route> routes() {
        return delegate.routes();
    }

    public Config updateDelegate(ConfigDelegate newDelegate) {
        this.delegate = newDelegate;
        return this;
    }
}

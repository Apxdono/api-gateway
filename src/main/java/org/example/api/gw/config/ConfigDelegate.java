package org.example.api.gw.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.example.api.gw.config.gateway.Endpoint;
import org.example.api.gw.config.gateway.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonDeserialize(builder = ConfigDelegate.Builder.class)
public class ConfigDelegate {
    private final GlobalConfig global;
    private final List<Endpoint> endpoints;
    private final List<Route> routes;
    private final Map<String, Endpoint> endpointMap;

    public static Builder create() {
        return new Builder();
    }

    private ConfigDelegate(Builder builder) {
        this.global = builder.global;
        this.endpoints = builder.endpoints;
        this.routes = builder.routes;
        this.endpointMap = builder.endpoints
                .stream().collect(Collectors.toMap(Endpoint::name, end -> end));
    }

    public GlobalConfig global() {
        return global;
    }

    public List<Endpoint> endpoints() {
        return endpoints;
    }

    public Map<String, Endpoint> endpointMap() {
        return endpointMap;
    }

    public List<Route> routes() {
        return routes;
    }

    @JsonIgnoreProperties(ignoreUnknown = false)
    @JsonPOJOBuilder
    public static final class Builder {
        private GlobalConfig global = GlobalConfig.create().build();

        private List<Endpoint> endpoints = new ArrayList<>();
        private List<Route> routes = new ArrayList<>();

        private Builder() {
        }

        @JsonSetter(value = "global", nulls = Nulls.SKIP)
        public Builder withGlobal(GlobalConfig global) {
            this.global = global;
            return this;
        }

        @JsonSetter(value = "endpoints", nulls = Nulls.SKIP)
        public Builder withEndpoints(List<Endpoint> endpoints) {
            this.endpoints = endpoints;
            return this;
        }

        @JsonSetter(value = "routes", nulls = Nulls.SKIP)
        public Builder withRoutes(List<Route> routes) {
            this.routes = routes;
            return this;
        }

        public ConfigDelegate build() {
            return new ConfigDelegate(this);
        }
    }
}

package org.example.api.gw.config.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.http.Handler;
import org.example.api.gw.utils.Checks;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@JsonDeserialize(builder = Route.Builder.class)
public class Route {
    private final String name;
    private final String routePath;
    private final RouteTarget target;
    private final Set<ALLOWED_METHOD> allowedMethods;

    public static Builder create() {
        return new Builder();
    }

    private Route(Builder builder) {
        this.routePath = builder.routePath;
        this.name = builder.name;
        this.target = builder.target;
        this.allowedMethods = builder.allowedMethods;
    }

    public String name() {
        return name;
    }

    public String routePath() {
        return routePath;
    }

    public String routePrefix() {
        return target.routePrefix(routePath);
    }

    public RouteTarget target() {
        return target;
    }

    public Set<ALLOWED_METHOD> allowedMethods() {
        return allowedMethods;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String name;
        private String routePath;
        private RouteTarget target;
        private Set<ALLOWED_METHOD> allowedMethods = new HashSet<>();

        private Builder() {
        }

        @JsonSetter(value = "name")
        public Builder withName(String name) {
            this.name = Checks.assertNotEmpty(name, "route name must be specified");
            return this;
        }

        @JsonSetter(value = "path")
        public Builder withRoutePath(String routePath) {
            this.routePath = Checks.assertNotEmpty(routePath, "route path must be specified");
            return this;
        }

        @JsonSetter(value = "target")
        public Builder withTarget(RouteTarget target) {
            this.target = Checks.assertNotNull(target, "route target must be specified");
            return this;
        }

        @JsonSetter(value = "methods", nulls = Nulls.SKIP)
        public Builder withAllowedMethods(Set<ALLOWED_METHOD> allowedMethods) {
            this.allowedMethods = allowedMethods;
            return this;
        }

        public Route build() {
            return new Route(this);
        }

        private void validate() {
            Checks.assertNotEmpty(name, "route name must be specified");
            Checks.assertNotEmpty(routePath, "route path must be specified");
            Checks.assertNotNull(target, "route target must be specified");
        }
    }

    public enum ALLOWED_METHOD {
        @JsonProperty("get")
        GET(handler -> ApiBuilder.get(handler)),
        @JsonProperty("post")
        POST(handler -> ApiBuilder.post(handler)),
        @JsonProperty("put")
        PUT(handler -> ApiBuilder.put(handler)),
        @JsonProperty("patch")
        PATCH(handler -> ApiBuilder.patch(handler)),
        @JsonProperty("delete")
        DELETE(handler -> ApiBuilder.delete(handler));

        private final Consumer<Handler> handlerConsumer;

        ALLOWED_METHOD(Consumer<Handler> handlerConsumer) {
            this.handlerConsumer = handlerConsumer;
        }

        public void handleMethod(Handler routeHandler) {
            this.handlerConsumer.accept(routeHandler);
        }
    }

}

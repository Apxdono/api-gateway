package org.example.api.gw.config.gateway;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.javalin.http.Context;
import org.example.api.gw.utils.Checks;

import java.util.Optional;
import java.util.function.Function;

@JsonDeserialize(builder = DirectRouteTarget.Builder.class)
public class DirectRouteTarget extends RouteTarget {

    private final String targetEndpoint;
    private final String path;
    private final PathMode pathMode;

    public static Builder create() {
        return new Builder();
    }

    private DirectRouteTarget(Builder builder) {
        this.type = Type.DIRECT;
        this.path = builder.path;
        this.targetEndpoint = builder.targetEndpoint;
        this.pathMode = builder.pathMode;
    }

    @Override
    public PathMode pathMode() {
        return pathMode;
    }

    @Override
    public String evaluatePath(Context ctx) {
        return path;
    }

    @Override
    public Optional<Endpoint> evaluateEndpoint(Context ctx, Function<String, Optional<Endpoint>> endpointResolver) {
        return endpointResolver.apply(targetEndpoint);
    }

    @Override
    public String routePrefix(String routePath) {
        return routePath;
    }

    @Override
    public String toString() {
        return String.format("direct target [endpoint: %s, path: '%s']", targetEndpoint, path);
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String targetEndpoint;
        private String path;
        private PathMode pathMode = PathMode.APPEND;

        private Builder() {
        }

        @JsonSetter("endpoint")
        public Builder withTargetEndpoint(String targetEndpoint) {
            this.targetEndpoint = Checks.assertNotEmpty(targetEndpoint, "target endpoint must be specified");
            return this;
        }

        @JsonSetter(value = "path", nulls = Nulls.SKIP)
        public Builder withPath(String path) {
            this.path = Checks.assertNotEmpty(path, "target path must not be empty");
            return this;
        }

        @JsonSetter(value = "path_mode", nulls = Nulls.SKIP)
        public Builder withPathMode(PathMode pathMode) {
            this.pathMode = pathMode;
            return this;
        }

        public DirectRouteTarget build() {
            Checks.assertNotEmpty(targetEndpoint, "target endpoint must be specified");
            return new DirectRouteTarget(this);
        }
    }
}

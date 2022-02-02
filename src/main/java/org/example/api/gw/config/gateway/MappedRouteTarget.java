package org.example.api.gw.config.gateway;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.javalin.http.Context;
import org.example.api.gw.utils.Checks;

import java.util.Optional;
import java.util.function.Function;

@JsonDeserialize(builder = MappedRouteTarget.Builder.class)
public class MappedRouteTarget extends RouteTarget {
    private final String endpointParam;
    private final String pathParam;
    private final PathMode pathMode;

    public static Builder create() {
        return new Builder();
    }

    private MappedRouteTarget(Builder builder) {
        this.type = Type.MAPPED;
        this.endpointParam = builder.endpointParam;
        this.pathParam = builder.pathParam;
        this.pathMode = builder.pathMode;
    }

    @Override
    public PathMode pathMode() {
        return pathMode;
    }

    @Override
    public String evaluatePath(Context ctx) {
        return ctx.pathParam(pathParam);
    }

    @Override
    public Optional<Endpoint> evaluateEndpoint(Context ctx, Function<String, Optional<Endpoint>> endpointResolver) {
        Checks.assertNotNull(endpointResolver, "Route target requires endpointResolver");
        return endpointResolver.apply(ctx.pathParam(endpointParam));
    }

    @Override
    public String routePrefix(String routePath) {
        int endpointPartIdx = routePath.indexOf("/{");
        if (endpointPartIdx < 0) {
            return null;
        }
        else {
            return routePath.substring(0, endpointPartIdx);
        }
    }

    @Override
    public String toString() {
        return String.format("mapped target [endpointParam: %s, pathParam: %s]", endpointParam, pathParam);
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private String endpointParam;
        private String pathParam;
        private PathMode pathMode = PathMode.APPEND;

        private Builder() {
        }

        @JsonSetter(value = "endpoint_param")
        public Builder withEndpointParam(String endpointParam) {
            this.endpointParam = Checks.assertNotEmpty(endpointParam, "route target endpoint_param is required");
            return this;
        }

        @JsonSetter(value = "path_param")
        public Builder withPathParam(String pathParam) {
            this.pathParam = Checks.assertNotEmpty(pathParam, "route target path_param is required");
            return this;
        }

        @JsonSetter(value = "path_mode", nulls = Nulls.SKIP)
        public Builder withPathMode(PathMode pathMode) {
            this.pathMode = pathMode;
            return this;
        }

        public MappedRouteTarget build() {
            validate();
            return new MappedRouteTarget(this);
        }

        private void validate() {
            Checks.assertNotEmpty(endpointParam, "route target endpoint_param is required");
            Checks.assertNotEmpty(pathParam, "route target path_param is required");
        }
    }
}

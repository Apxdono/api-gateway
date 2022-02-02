package org.example.api.gw.config.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.javalin.http.Context;

import java.util.Optional;
import java.util.function.Function;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = MappedRouteTarget.class, name = "mapped"),
        @JsonSubTypes.Type(value = DirectRouteTarget.class, name = "direct")
})
public abstract class RouteTarget {
    protected Type type;
    public abstract PathMode pathMode();
    public abstract String evaluatePath(Context ctx);
    public abstract Optional<Endpoint> evaluateEndpoint(Context ctx, Function<String, Optional<Endpoint>> endpointResolver);
    public abstract String routePrefix(String routePath);

    public Type type() {
        return type;
    }

    public enum Type {
        @JsonProperty("mapped")
        MAPPED,
        @JsonProperty("direct")
        DIRECT
    }

    public enum PathMode {
        @JsonProperty("append")
        APPEND,
        @JsonProperty("rewrite")
        REWRITE,
        @JsonProperty("ignore")
        IGNORE;
    }
}

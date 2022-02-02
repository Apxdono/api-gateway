package org.example.api.gw.contexts;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.example.api.gw.config.gateway.Endpoint;
import org.example.api.gw.config.gateway.Route;
import org.example.api.gw.utils.Checks;

import java.util.function.Supplier;

public class RouteContext {

    private final Route route;
    private final Endpoint endpoint;
    private final HttpUrl endpointUrl;
    private final Supplier<RequestBody> bodySupplier;
    private final String method;
    private final MediaType contentType;


    public static Builder create() {
        return new Builder();
    }

    private RouteContext(Builder builder) {
        this.route = builder.route;
        this.endpoint = builder.endpoint;
        this.endpointUrl = builder.endpointUrl;
        this.bodySupplier = builder.bodySupplier;
        this.method = builder.method;
        this.contentType = builder.contentType;
    }

    public Route route() {
        return route;
    }

    public Endpoint endpoint() {
        return endpoint;
    }

    public HttpUrl endpointUrl() {
        return endpointUrl;
    }

    public Supplier<RequestBody> bodySupplier() {
        return bodySupplier;
    }

    public String method() {
        return method;
    }

    public MediaType contentType() {
        return contentType;
    }

    public static final class Builder {
        private Route route;
        private Endpoint endpoint;
        private HttpUrl endpointUrl;
        private Supplier<RequestBody> bodySupplier;
        private String method;
        private MediaType contentType;

        private Builder() {
        }

        public Builder withRoute(Route route) {
            this.route = route;
            return this;
        }

        public Builder withEndpoint(Endpoint endpoint) {
            this.endpoint = endpoint;
            return this;
        }

        public Builder withEndpointUrl(HttpUrl endpointUrl) {
            this.endpointUrl = endpointUrl;
            return this;
        }

        public Builder withMethod(String method) {
            this.method = method;
            return this;
        }

        public Builder withBody(Supplier<RequestBody> bodySupplier) {
            this.bodySupplier = bodySupplier;
            return this;
        }

        public Builder withContentType(MediaType contentType) {
            this.contentType = contentType;
            return this;
        }

        public RouteContext build() {
            validate();
            return new RouteContext(this);
        }

        private void validate() {
            Checks.assertNotNull(route, "route context requires actual route");
            Checks.assertNotNull(endpoint, "route context requires endpoint");
            Checks.assertNotNull(endpointUrl, "route context requires valid endpoint url");
            Checks.assertNotNull(bodySupplier, "route context requires body supplier");
            Checks.assertNotNull(method, "route context requires proper http method");
            Checks.assertNotNull(contentType, "route context requires proper content type");
        }
    }
}

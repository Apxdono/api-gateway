package org.example.api.gw.config.http;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = RequestLimitConfig.Builder.class)
public class RequestLimitConfig {
    private final int maximum;
    private final int perHost;

    public static Builder create() {
        return new Builder();
    }

    private RequestLimitConfig(Builder builder) {
        this.maximum = builder.maximum;
        this.perHost = builder.perHost;
    }

    public int max() {
        return maximum;
    }

    public int maxPerHost() {
        return perHost;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private int maximum = 64;
        private int perHost = 10;

        private Builder() {
        }

        @JsonSetter(value = "max", nulls = Nulls.SKIP)
        public Builder withMaxRequests(int maxRequests) {
            this.maximum = maxRequests;
            return this;
        }

        @JsonSetter(value = "max_per_host", nulls = Nulls.SKIP)
        public Builder withMaxRequestsPerHosts(int maxRequestsPerHosts) {
            this.perHost = maxRequestsPerHosts;
            return this;
        }

        public RequestLimitConfig build() {
            return new RequestLimitConfig(this);
        }
    }
}

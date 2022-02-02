package org.example.api.gw.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.example.api.gw.config.http.HttpConfig;
import org.example.api.gw.config.http.RequestLimitConfig;
import org.example.api.gw.config.http.Timeout;

@JsonDeserialize(builder = GlobalConfig.Builder.class)
public class GlobalConfig {
    private static final HttpConfig DEFAULT_INSTANCE = HttpConfig.create()
            .withMaxIdle(20)
            .withKeepAliveDuration("60s")
            .withTimeout(Timeout.DEFAULTS)
            .withRequestsConfig(RequestLimitConfig.create().build())
            .build();

    private final int port;
    private final HttpConfig httpConfig;

    public static Builder create() {
        return new Builder();
    }

    private GlobalConfig(Builder builder) {
        this.port = builder.port;
        this.httpConfig = builder.httpConfig;
    }

    public int port() {
        return port;
    }

    public HttpConfig httpConfig() {
        return httpConfig;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private int port = 4000;
        private HttpConfig httpConfig = DEFAULT_INSTANCE;

        private Builder() {
        }

        @JsonSetter(value = "port", nulls = Nulls.SKIP)
        public Builder withPort(int port) {
            this.port = port;
            return this;
        }

        @JsonSetter(value = "http", nulls = Nulls.SKIP)
        public Builder withHttpConfig(HttpConfig httpConfig) {
            this.httpConfig = httpConfig;
            return this;
        }

        public GlobalConfig build() {
            return new GlobalConfig(this);
        }
    }
}

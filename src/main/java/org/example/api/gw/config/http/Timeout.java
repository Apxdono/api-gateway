package org.example.api.gw.config.http;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.example.api.gw.utils.DurationHelper;

import java.time.Duration;

@JsonDeserialize(builder = Timeout.Builder.class)
public class Timeout {
    private static final String DEFAULT_TIME = "10s";
    public static Timeout DEFAULTS = Timeout.create()
            .withConnectTimeout(DEFAULT_TIME)
            .withReadTimeout(DEFAULT_TIME)
            .build();

    private final Duration connectTimeout;
    private final Duration readTimeout;
    private final Duration cappedTimeout;

    public static Builder create() {
        return new Builder();
    }

    private Timeout(Builder builder) {
        this.connectTimeout = builder.connectTimeout;
        this.readTimeout = builder.readTimeout;
        this.cappedTimeout = builder.cappedTimeout;
    }

    public Duration connect() {
        return connectTimeout;
    }

    public Duration read() {
        return readTimeout;
    }

    public Duration cappedAt() {
        return cappedTimeout;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private Duration connectTimeout = DurationHelper.toDuration(DEFAULT_TIME);
        private Duration readTimeout = DurationHelper.toDuration(DEFAULT_TIME);
        private Duration cappedTimeout = DurationHelper.toDuration("0s");

        private Builder() {
        }

        @JsonSetter(value = "connect", nulls = Nulls.SKIP)
        public Builder withConnectTimeout(String connectTimeout) {
            this.connectTimeout = DurationHelper.toDuration(connectTimeout);
            return this;
        }

        @JsonSetter(value = "read", nulls = Nulls.SKIP)
        public Builder withReadTimeout(String readTimeout) {
            this.readTimeout = DurationHelper.toDuration(readTimeout);
            return this;
        }

        @JsonSetter(value = "capped_at", nulls = Nulls.SKIP)
        public Builder withCappedTimeout(String cappedTimeout) {
            this.cappedTimeout = DurationHelper.toDuration(cappedTimeout);
            return this;
        }

        public Timeout build() {
           return new Timeout(this);
        }
    }
}

package org.example.api.gw.config.http;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.example.api.gw.utils.DurationHelper;

import java.time.Duration;

@JsonDeserialize(builder = HttpConfig.Builder.class)
public class HttpConfig {
    private final Timeout timeout;
    private final Duration keepAliveDuration;
    private final int maxIdle;
    private final RequestLimitConfig requests;
    private final boolean followRedirects;

    public HttpConfig(Builder builder) {
        this.timeout = builder.timeout;
        this.requests = builder.requests;
        this.maxIdle = builder.maxIdle;
        this.keepAliveDuration = builder.keepAliveDuration;
        this.followRedirects = builder.followRedirects;
    }

    public static Builder create() {
        return new Builder();
    }

    public Timeout timeout() {
        return timeout;
    }

    public Duration keepAliveDuration() {
        return keepAliveDuration;
    }

    public int maxIdle() {
        return maxIdle;
    }

    public RequestLimitConfig requests() {
        return requests;
    }

    public boolean followRedirects() {
        return followRedirects;
    }

    public static final class Builder {
        private boolean followRedirects = false;
        private Timeout timeout;
        private Duration keepAliveDuration = DurationHelper.toDuration("60s");
        private int maxIdle = 100;
        private RequestLimitConfig requests;

        private Builder() {
        }

        @JsonSetter(value = "timeouts", nulls = Nulls.SKIP)
        public Builder withTimeout(Timeout timeout) {
            this.timeout = timeout;
            return this;
        }

        @JsonSetter(value = "keep_alive", nulls = Nulls.SKIP)
        public Builder withKeepAliveDuration(String keepAliveDuration) {
            this.keepAliveDuration = DurationHelper.toDuration(keepAliveDuration);;
            return this;
        }

        @JsonSetter(value = "max_idle", nulls = Nulls.SKIP)
        public Builder withMaxIdle(int keepAliveCount) {
            this.maxIdle = keepAliveCount;
            return this;
        }

        @JsonSetter(value = "requests", nulls = Nulls.SKIP)
        public Builder withRequestsConfig(RequestLimitConfig requests) {
            this.requests = requests;
            return this;
        }

        @JsonSetter(value = "follow_redirects", nulls = Nulls.SKIP)
        public Builder withFollowRedirects(boolean followRedirects) {
            this.followRedirects = followRedirects;
            return this;
        }

        public HttpConfig build() {
            return new HttpConfig(this);
        }
    }
}

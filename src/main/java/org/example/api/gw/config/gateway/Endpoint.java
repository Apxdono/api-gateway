package org.example.api.gw.config.gateway;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import okhttp3.HttpUrl;
import org.example.api.gw.config.http.HttpConfig;

import java.util.Objects;

import static org.example.api.gw.utils.Checks.*;

@JsonDeserialize(builder = Endpoint.Builder.class)
public class Endpoint {
    private final String name;
    private final String alias;
    private final HttpUrl url;
    private final HttpConfig httpConfig;

    public static Builder create() {
        return new Builder();
    }

    private Endpoint(Builder builder) {
        this.url = builder.url;
        this.name = builder.name;
        this.alias = Objects.toString(builder.alias, name);
        this.httpConfig = builder.httpConfig;
    }

    public String name() {
        return name;
    }

    public HttpUrl url() {
        return url;
    }

    public String alias() {
        return alias;
    }

    public HttpConfig httpConfig() {
        return httpConfig;
    }

    @JsonPOJOBuilder
    public static final class Builder {
        private HttpConfig httpConfig;
        private String name;
        private String alias;
        private HttpUrl url;

        private Builder() {
        }

        @JsonSetter(value = "name")
        public Builder withName(String name) {
            this.name = assertNotEmpty(name, "Endpoint name is mandatory. " + actualString(name));
            return this;
        }

        @JsonSetter(value = "url")
        public Builder withUrl(HttpUrl baseUrl) {
            this.url = assertNotNull(baseUrl, "Invalid endpoint url");
            return this;
        }

        @JsonSetter(value = "alias", nulls = Nulls.SKIP)
        public Builder withAlias(String alias) {
            this.alias = alias;
            return this;
        }

        @JsonSetter(value = "http", nulls = Nulls.SKIP)
        public Builder withHttpConfig(HttpConfig httpConfig) {
            this.httpConfig = httpConfig;
            return this;
        }

        public Endpoint build() {
            validate();
            return new Endpoint(this);
        }

        private void validate() {
            assertNotEmpty(name, "Endpoint name is mandatory. " + actualString(name));
            assertNotNull(url, "Invalid endpoint url");
        }
    }



}

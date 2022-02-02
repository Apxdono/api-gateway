package org.example.api.gw.contexts;

import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProxyContext {
    private static final String PROXY_MATCHED_ATTR = "gateway.proxy.already.matched";
    private static final String PROXY_HEADERS_ATTR = "gateway.proxy.request.headers";
    private static final String LOGGING_FIELDS_ATTR = "gateway.logging.fields";

    private final Context ctx;
    private final Map<String, String> proxyHeaders;
    private final Map<String, String> loggingFields;

    public static ProxyContext using(Context ctx) {
        return new ProxyContext(ctx, getOrInit(ctx, PROXY_MATCHED_ATTR), getOrInit(ctx, LOGGING_FIELDS_ATTR));
    }

    private ProxyContext(Context ctx, Map<String, String> headers, Map<String, String> loggingFields) {
        this.ctx = ctx;
        this.proxyHeaders = headers;
        this.loggingFields = loggingFields;

    }

    public boolean isComplete() {
        return false;
    }

    public ProxyContext addProxyHeader(String name, String value) {
        proxyHeaders.put(name, value);
        return this;
    }

    public ProxyContext addLoggingField(String name, Object value) {
        loggingFields.put(name, Objects.toString(value));
        return this;
    }

    public Map<String, String> proxyHeaders() {
        return proxyHeaders;
    }

    private static Map<String, String> getOrInit(Context ctx, String attributeName) {
        Map<String, String> map = ctx.attribute(PROXY_MATCHED_ATTR);
        if (map == null) {
            map = new HashMap<>();
            ctx.attribute(PROXY_MATCHED_ATTR, map);
        }
        return map;
    }
}

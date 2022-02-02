package org.example.api.gw.utils;

import io.javalin.http.Context;
import okhttp3.HttpUrl;
import org.example.api.gw.config.gateway.RouteTarget;

import java.util.function.Supplier;

public class HttpUrlHelper {

    public static HttpUrl mergeWithPath(HttpUrl endpointUrl, Supplier<String> extraPath, RouteTarget.PathMode pathProcessMode) {
        var urlBuilder = endpointUrl.newBuilder();
        if (pathProcessMode == RouteTarget.PathMode.APPEND) {
            urlBuilder.addPathSegments(extraPath.get());
        }
        else if (pathProcessMode == RouteTarget.PathMode.REWRITE) {
//                urlBuilder.setPathSegment(0, path);
        }
        return urlBuilder.build();
    }

    public static HttpUrl copyQueryParams(Context ctx, HttpUrl httpUrl) {
        var builder = httpUrl.newBuilder();
        ParamConverter.convertMultiValueParams(ctx.queryParamMap(), builder::addQueryParameter);
        return builder.build();
    }
}

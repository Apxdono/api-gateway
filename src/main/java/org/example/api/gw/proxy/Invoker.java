package org.example.api.gw.proxy;

import io.javalin.core.util.Header;
import io.javalin.http.Context;
import okhttp3.*;
import org.example.api.gw.contexts.ProxyContext;
import org.example.api.gw.contexts.RouteContext;
import org.example.api.gw.exceptions.InvokerException;
import org.example.api.gw.utils.ParamConverter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.CompletableFuture;

public class Invoker {
    private static final Logger LOG = LoggerFactory.getLogger(Invoker.class);

    private final EndpointClients clients;

    public Invoker(EndpointClients clients) {
        this.clients = clients;
    }

    public void invokeRequest(Context ctx, RouteContext requestCtx) {
        var endpoint = requestCtx.endpoint();
        var requestBuilder = new Request.Builder()
                .url(requestCtx.endpointUrl());
        var client = clients.getClient(endpoint);

        // TODO: add extra header processing
        ParamConverter.convertParams(ctx.headerMap(), requestBuilder::addHeader);
        ParamConverter.convertParams(ProxyContext.using(ctx).proxyHeaders(), requestBuilder::addHeader);

        requestBuilder.method(requestCtx.method(), requestCtx.bodySupplier().get());

        syncProcessing(ctx, client, requestBuilder, endpoint.name(), ctx.path());
//        asyncProcessing(ctx, client, requestBuilder, endpoint.name(), ctx.path());
    }

    private static void processResponse(Context ctx, Response response) {
        ctx.header(Header.CONTENT_TYPE, response.header(Header.CONTENT_TYPE));
        ctx.result(response.body().byteStream());
        LOG.info("Request succeeded");
    }

    private void syncProcessing(Context ctx, OkHttpClient client, Request.Builder requestBuilder, String endpointName,
                                String ctxPath) {
        try {
            processResponse(ctx, client.newCall(requestBuilder.build()).execute());
        }
        catch (SocketTimeoutException e) {
            LOG.error("Timeout waiting for " + endpointName, e);
            throw new InvokerException(504, "Timeout waiting for " + ctxPath);
        }
        catch (IOException e) {
            LOG.error("Call to " + endpointName + " has failed", e);
            throw new InvokerException(500, "Unable to request data from " + ctxPath);
        }
    }

    private void asyncProcessing(Context ctx, OkHttpClient client, Request.Builder requestBuilder,
                                 String endpointName, String ctxPath) {
        CompletableFuture<Response> responseFuture = new CompletableFuture<>();
        ctx.future(responseFuture, r -> {
            if (r != null) {
                processResponse(ctx, (Response) r);
            }
            else {
                LOG.info("Request failed");
            }
        });
        client.newCall(requestBuilder.build()).enqueue(toCallback(ctx.path(), endpointName, responseFuture));
    }

    private static Callback toCallback(String ctxPath, String endpointName, CompletableFuture<Response> future) {
        return new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                if (e instanceof SocketTimeoutException) {
                    LOG.error("Timeout waiting for " + endpointName, e);
                    future.completeExceptionally(new InvokerException(504, "Timeout waiting for " + ctxPath));
                }
                else {
                    LOG.error("Call to " + endpointName + " has failed", e);
                    future.completeExceptionally(new InvokerException(500, "Unable to request data from " + ctxPath));
                }
            }

            @Override public void onResponse(@NotNull Call call, @NotNull Response response) {
                future.complete(response);
            }
        };
    }
}

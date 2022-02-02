package org.example.api.gw.handlers;

import io.javalin.http.Handler;

public class Handlers {
    private static final ForwardDetailsHandler FORWARD_DETAILS_HANDLER = new ForwardDetailsHandler();
    private static final CorrelationIdHandler CORRELATION_ID_HANDLER = new CorrelationIdHandler();

    public static Handler forwardInfoHandler() {
        return FORWARD_DETAILS_HANDLER;
    }

    public static Handler correlationIdHandler() {
        return CORRELATION_ID_HANDLER;
    }
}

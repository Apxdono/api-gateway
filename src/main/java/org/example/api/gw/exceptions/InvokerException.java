package org.example.api.gw.exceptions;

import io.javalin.http.HttpResponseException;

public class InvokerException extends HttpResponseException {

    public InvokerException(int code, String message) {
        super(code, message);
    }
}

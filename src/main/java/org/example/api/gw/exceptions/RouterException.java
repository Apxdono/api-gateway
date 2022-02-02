package org.example.api.gw.exceptions;

import io.javalin.http.HttpResponseException;

public class RouterException extends HttpResponseException {

    public RouterException(String message) {
        super(504, message);
    }
}

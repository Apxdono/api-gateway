package org.example.api.gw.exceptions;

import io.javalin.http.HttpResponseException;

public class EndpointException extends HttpResponseException {

    public EndpointException(String message) {
        super(404, message);
    }
}

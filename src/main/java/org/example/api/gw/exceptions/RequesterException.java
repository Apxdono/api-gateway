package org.example.api.gw.exceptions;

import io.javalin.http.HttpResponseException;

public class RequesterException extends HttpResponseException {

    public RequesterException(String message) {
        super(500, message);
    }
}

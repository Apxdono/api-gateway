package org.example.api.gw.exceptions;

import io.javalin.http.HttpResponseException;

public class MediaException extends HttpResponseException {

    public MediaException(String message) {
        super(400, message);
    }
}

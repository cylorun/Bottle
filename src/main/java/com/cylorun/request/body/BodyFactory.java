package com.cylorun.request.body;

import com.cylorun.request.enums.ContentType;

import java.util.Optional;

public class BodyFactory {
    public static Optional<RequestBody> createBody(String data, ContentType contentType) {
        if (contentType == null) {
            return Optional.empty();
        }
        if (contentType.equals(ContentType.APPLICATION_JSON)) {
            return Optional.of(new JsonBody(data));
        }
        System.out.println("unknown body type: " + contentType);
        return Optional.empty();
    }
}

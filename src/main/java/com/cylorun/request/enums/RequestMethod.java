package com.cylorun.request.enums;

public enum RequestMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE");
    private final String name;

    private RequestMethod(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

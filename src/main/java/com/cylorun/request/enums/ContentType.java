package com.cylorun.request.enums;

public enum ContentType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    TEXT_PLAIN("text/plain");

    private final String name;
    private ContentType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

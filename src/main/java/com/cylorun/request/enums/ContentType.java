package com.cylorun.request.enums;

import java.util.Optional;

public enum ContentType {
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css"),
    APPLICATION_JAVASCRIPT("application/javascript"),
    APPLICATION_JSON("application/json"),
    TEXT_PLAIN("text/plain");

    private final String name;
    private ContentType(String name) {
        this.name = name;
    }

    public static Optional<ContentType> fromName(String name) {
        for (ContentType type : ContentType.values()) {
            if (type.name.equals(name)) {
                return Optional.of(type);
            }
        }
        return Optional.empty();
    }
    @Override
    public String toString() {
        return this.name;
    }
}

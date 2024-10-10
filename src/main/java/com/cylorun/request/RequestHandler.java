package com.cylorun.request;

import java.io.FileNotFoundException;

public interface RequestHandler {
    void handle(Request request, Response response, Runnable next) throws FileNotFoundException;

    @FunctionalInterface
    interface BasicRequestHandler extends RequestHandler {
        void handle(Request request, Response response) throws FileNotFoundException;

        @Override
        default void handle(Request request, Response response, Runnable next) throws FileNotFoundException {
            this.handle(request, response);
            next.run();
        }
    }

    @FunctionalInterface
    interface MiddlewareRequestHandler extends RequestHandler {
        @Override
        void handle(Request request, Response response, Runnable next);
    }
}

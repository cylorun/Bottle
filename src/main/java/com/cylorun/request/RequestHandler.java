package com.cylorun.request;

public interface RequestHandler {
    void handle(Request request, Response response, Runnable next);

    @FunctionalInterface
    interface BasicRequestHandler extends RequestHandler {
        void handle(Request request, Response response);

        @Override
        default void handle(Request request, Response response, Runnable next) {
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

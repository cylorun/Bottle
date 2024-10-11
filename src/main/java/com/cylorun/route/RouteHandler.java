package com.cylorun.route;

import com.cylorun.request.RequestHandler;
import com.cylorun.request.enums.RequestMethod;

import java.util.*;

public class RouteHandler {
    public final Map<Route, RequestHandler> routes;

    public RouteHandler() {
        this.routes = new LinkedHashMap<>();
    }

    public void addRoute(RequestMethod method, String path, RequestHandler handler) {
        this.routes.put(new Route(method, path), handler);
    }

    public List<RequestHandler> getHandlers(String method, String path) {
        List<RequestHandler> handlers = new ArrayList<>();
        for (Map.Entry<Route, RequestHandler> entry : this.routes.entrySet()) {
            if ((entry.getKey().path.equals(path) && entry.getKey().method == RequestMethod.valueOf(method))
                    || (entry.getKey().method == RequestMethod.ANY && entry.getKey().path.equals("*"))
                    || entry.getKey().method == RequestMethod.ANY && entry.getKey().path.equals(path)) {
                handlers.add(entry.getValue());
            }
        }
        return handlers;
    }
}

package com.cylorun.route;

import com.cylorun.request.Request;
import com.cylorun.request.RequestHandler;
import com.cylorun.request.enums.RequestMethod;

import java.util.HashMap;
import java.util.Map;

public class RouteHandler {
    private Map<Route, RequestHandler> routes;

    public RouteHandler() {
        this.routes = new HashMap<>();
    }

    public void addRoute(String method, String path, RequestHandler handler) {
        this.routes.put(new Route(RequestMethod.valueOf(method), path), handler);
    }

    public RequestHandler getHandler(String method, String path) {
        for (Map.Entry<Route, RequestHandler> entry : this.routes.entrySet()) {
            if (entry.getKey().path.equals(path) && entry.getKey().method == RequestMethod.valueOf(method)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public RequestHandler getHandler(Route route) {
        return routes.get(route);
    }
}

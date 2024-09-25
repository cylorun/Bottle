package com.cylorun.route;

import com.cylorun.request.Request;
import com.cylorun.request.enums.RequestMethod;


public class Route {
    public final RequestMethod method;
    public final String path;

    public Route(RequestMethod method, String path) {
        this.method = method;
        this.path = path;
    }
}

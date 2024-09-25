package com.cylorun.request;


import com.cylorun.request.enums.RequestMethod;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private RequestMethod method;
    private String path;
    private Query query;

    private Request(RequestMethod method, String path, Query queryParams) {
        this.method = method;
        this.path = path;
        this.query = queryParams;
    }

    public static Request fromUrl(String fullPath, String method) {
        return fromUrl(fullPath, RequestMethod.valueOf(method.toUpperCase()));
    }

    public static Request fromUrl(String fullPath, RequestMethod method) {
        Query query = Query.fromPath(fullPath);
        String path = fullPath.split("\\?")[0];
        return new Request(method, path, query);
    }

    public RequestMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Query getQuery() {
        return this.query;
    }

    public static class Query extends HashMap<String, String> {

        public Query(Map<String, String> queries) {
            this.putAll(queries);
        }

        public static Query fromPath(String path) {
            Map<String, String> queryParams = new HashMap<>();
            if (path.contains("?")) {
                String queryString = path.split("\\?")[1];
                for (String param : queryString.split("&")) {
                    String[] kv = param.split("=");
                    queryParams.put(kv[0], kv[1]);
                }
            }
            return new Query(queryParams);
        }
    }
}
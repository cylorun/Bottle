package com.cylorun.request;


import com.cylorun.request.body.BodyFactory;
import com.cylorun.request.body.RequestBody;
import com.cylorun.request.enums.ContentType;
import com.cylorun.request.enums.RequestMethod;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Request {
    private RequestMethod method;
    private ContentType contentType;
    private final String path;
    private final Query query;
    private final RequestBody body;
    private Map<String, String> headers;

    private Request(RequestMethod method, Map<String, String> headers, String path, Query queryParams, RequestBody body) {
        this.method = method;
        this.headers = headers;
        this.path = path;
        this.query = queryParams;
        this.body = body;

        if (this.headers.containsKey("Content-Type")) {
            this.contentType = ContentType.fromName(this.headers.get("Content-Type").trim()).orElse(null);
        }
    }


    public static Request fromBuffer(String requestLine, BufferedReader in) {
        String[] requestParts = requestLine.split(" ");
        String fullRequestPath = requestParts[1];

        RequestMethod method = RequestMethod.valueOf(requestParts[0].trim());
        Query queryParams = Query.fromPath(fullRequestPath);

        Map<String, String> headers = parseHeadersFromBuffer(in);

        String contentLenHeader = headers.get("Content-Length");
        int contentLength =  contentLenHeader == null ? 0 : Integer.parseInt(contentLenHeader.trim());
        String requestBodyStr = getBodyFromBuffer(in, contentLength).orElse(null);
        RequestBody reqBody = BodyFactory.createBody(requestBodyStr, ContentType.fromName("Content-Type").orElse(null)).orElse(null);

        return new Request(method, headers, fullRequestPath, queryParams, reqBody);
    }

    private static Map<String, String> parseHeadersFromBuffer(BufferedReader in) {
        Map<String, String> headers = new HashMap<>();
        String line;
        try {
            while (!(line = in.readLine()).isEmpty()) {
                String key = line.split(":")[0];
                String value = line.split(":")[1];
                headers.put(key, value);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse headers");
        }

        return headers;
    }

    private static Optional<String> getBodyFromBuffer(BufferedReader in, int contentLength) {
        try {
            StringBuilder requestBody = new StringBuilder();
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                in.read(bodyChars, 0, contentLength);
                requestBody.append(bodyChars);
            }

            return Optional.ofNullable(requestBody.toString().isEmpty() ? null : requestBody.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse body from request");
        }
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

    public Optional<RequestBody> getBody() {
        return Optional.ofNullable(this.body);
    }

    public Optional<ContentType> getContentType() {
        return Optional.ofNullable(this.contentType);
    }

    public Map<String, String> getHeaders() {
        return this.headers;
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
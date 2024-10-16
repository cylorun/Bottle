package com.cylorun;

import com.cylorun.request.Request;
import com.cylorun.request.RequestHandler;
import com.cylorun.request.Response;
import com.cylorun.request.enums.RequestMethod;
import com.cylorun.route.RouteHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Bottle {

    private boolean isRunning;
    private ServerSocket socketServer;
    private final RouteHandler routeHandler;
    private final int port;

    public Bottle(int port) {
        this.routeHandler = new RouteHandler();
        this.port = port;
    }

    public void start() {
        if (this.isRunning) {
            return;
        }
        try {
            this.socketServer = new ServerSocket(this.port);
            this.isRunning = true;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to start server: " + e.getMessage());
        }

        while (true) {
            try (Socket clientSocket = this.socketServer.accept();
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                String requestLine = in.readLine();
                if (requestLine == null) continue;
                String[] requestParts = requestLine.split(" ");
                String method = requestParts[0];
                String fullPath = requestParts[1];
                String path = fullPath.split("\\?")[0];

                Request request = Request.fromBuffer(requestLine, in);
                Response response = new Response();

                if (path.endsWith(".css") || path.endsWith(".js")) {
                    response.serveStatic("static" + path);
                } else {
                    List<RequestHandler> handlers = this.routeHandler.getHandlers(method, path);
                    if (!handlers.isEmpty()) {
                        AtomicBoolean b = new AtomicBoolean(false);
                        for (RequestHandler handler : handlers) {
                            if (handler instanceof RequestHandler.BasicRequestHandler basicHandler) {
                                basicHandler.handle(request, response);
                                break; // only one basic req handler per path
                            }
                            if (handler instanceof RequestHandler.MiddlewareRequestHandler middlewareHandler) {
                                middlewareHandler.handle(request, response, () -> {
                                    b.set(true);
                                });
                            }
                        }
                    }

                    if (handlers.stream().allMatch(h -> h instanceof RequestHandler.MiddlewareRequestHandler)){

                    }
                }

                out.write(response.getResponse());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void use(RequestHandler.MiddlewareRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.ANY, "*", handler);
    }

    public void use(String path, RequestHandler.MiddlewareRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.ANY, path, handler);
    }

    public void use(RequestHandler.BasicRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.ANY, "*", handler);
    }

    public void use(String path, RequestHandler.BasicRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.ANY, path, handler);
    }


    public void get(String path, RequestHandler.BasicRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.GET, path, handler);
    }

    public void post(String path, RequestHandler.BasicRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.POST, path, handler);
    }

    public void put(String path, RequestHandler.BasicRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.PUT, path, handler);
    }

    public void delete(String path, RequestHandler.BasicRequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.DELETE, path, handler);
    }

    public static void main(String[] args) {
        Bottle btl = new Bottle(9999);
        btl.get("/", (req, res) -> {
            res.renderHTML("index.html");
        });


        btl.post("/", (req, res) -> {
            res.json("{\"jojoe\": 1}");
        });

        btl.get("/debug", (req, res) -> {
            res.writeBody(String.format("Headers: %s\nRequest Query: %s \n", req.getHeaders(), req.getQuery()));
        });

        btl.use((req, res) -> {
            res.setStatus(404);
            try {
                res.renderHTML("not-found.html");
            } catch (FileNotFoundException e) {
                System.out.println("not-found.html file not found. ok.");
            }
        });

        btl.use("/appl", (req, res, next) -> {
            next.run();
        });

        btl.start();
    }
}
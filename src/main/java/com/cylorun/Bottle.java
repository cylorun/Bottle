package com.cylorun;

import com.cylorun.request.Request;
import com.cylorun.request.RequestHandler;
import com.cylorun.request.Response;
import com.cylorun.request.enums.RequestMethod;
import com.cylorun.route.RouteHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
                    RequestHandler handler = this.routeHandler.getHandler(method, path);
                    if (handler != null) {
                        handler.handle(request, response);
                    } else {
                        response.setStatus(404);
                        response.writeBody("404 Not Found");
                    }
                }

                out.write(response.getResponse());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void get(String path, RequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.GET, path, handler);
    }

    public void post(String path, RequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.POST, path, handler);
    }

    public void put(String path, RequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.PUT, path, handler);
    }

    public void delete(String path, RequestHandler handler) {
        this.routeHandler.addRoute(RequestMethod.DELETE, path, handler);
    }

    public static void main(String[] args) {
        Bottle btl = new Bottle(9999);
        btl.get("/", (req, res) -> {
            res.renderHTML("templates/not-found.html");
        });

        btl.post("/" , (req, res) -> {

        });

        btl.get("/debug", (req, res) -> {
            res.writeBody(String.format("Headers: %s\nRequest Query: %s \n", req.getHeaders(), req.getQuery()));
        });

        btl.start();
    }
}
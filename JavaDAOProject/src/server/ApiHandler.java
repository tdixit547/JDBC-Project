package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class ApiHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        try {
            handleRequest(exchange);
        } catch (Exception e) {
            sendError(exchange, 500, e.getMessage() != null ? e.getMessage() : "Internal server error");
        }
    }

    protected abstract void handleRequest(HttpExchange exchange) throws Exception;

    protected void sendJson(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        sendJson(exchange, statusCode, JsonHelper.errorJson(message));
    }

    protected String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            return baos.toString(StandardCharsets.UTF_8.name());
        }
    }

    protected String getPathParam(HttpExchange exchange, int index) {
        String[] parts = exchange.getRequestURI().getPath().split("/");
        if (index < parts.length) return parts[index];
        return null;
    }

    protected int getPathParamInt(HttpExchange exchange, int index) {
        String param = getPathParam(exchange, index);
        if (param == null) throw new IllegalArgumentException("Missing path parameter at index " + index);
        return Integer.parseInt(param);
    }
}

package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.*;

public class StaticFileHandler implements HttpHandler {

    private final String webRoot;

    public StaticFileHandler(String webRoot) {
        this.webRoot = webRoot;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Add CORS headers for image requests
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

        String path = exchange.getRequestURI().getPath();
        // Strip the context path (e.g. /images/food_1.png -> /food_1.png)
        String contextPath = exchange.getHttpContext().getPath();
        if (path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        if (path.equals("/") || path.isEmpty()) path = "/index.html";

        File file = new File(webRoot, path);
        if (!file.exists() || file.isDirectory()) {
            file = new File(webRoot, "index.html");
        }

        if (!file.exists()) {
            String response = "404 Not Found";
            exchange.sendResponseHeaders(404, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
            return;
        }

        String contentType = getContentType(file.getName());
        exchange.getResponseHeaders().set("Content-Type", contentType);
        exchange.sendResponseHeaders(200, file.length());
        try (OutputStream os = exchange.getResponseBody();
             FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
        }
    }

    private String getContentType(String fileName) {
        if (fileName.endsWith(".html")) return "text/html; charset=UTF-8";
        if (fileName.endsWith(".css")) return "text/css; charset=UTF-8";
        if (fileName.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (fileName.endsWith(".json")) return "application/json";
        if (fileName.endsWith(".png")) return "image/png";
        if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return "image/jpeg";
        if (fileName.endsWith(".svg")) return "image/svg+xml";
        if (fileName.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}

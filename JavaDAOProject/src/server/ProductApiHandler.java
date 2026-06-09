package server;

import com.sun.net.httpserver.HttpExchange;
import model.Product;
import service.ProductService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Base64;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class ProductApiHandler extends ApiHandler {

    private final ProductService productService;

    public ProductApiHandler(ProductService productService) {
        this.productService = productService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String[] parts = exchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                if (parts.length <= 3) {
                    listAll(exchange);
                } else {
                    getById(exchange, Integer.parseInt(parts[3]));
                }
                break;
            case "POST":
                addProduct(exchange);
                break;
            case "PUT":
                if (parts.length >= 5 && "stock".equals(parts[4])) {
                    updateStock(exchange, Integer.parseInt(parts[3]));
                } else {
                    sendError(exchange, 400, "Invalid endpoint");
                }
                break;
            case "DELETE":
                deleteProduct(exchange, Integer.parseInt(parts[3]));
                break;
            default:
                sendError(exchange, 405, "Method not allowed");
        }
    }

    private void listAll(HttpExchange exchange) throws Exception {
        List<Product> products = productService.getAllProducts();
        sendJson(exchange, 200, JsonHelper.toJsonArray(products, JsonHelper::toJson));
    }

    private void getById(HttpExchange exchange, int id) throws Exception {
        try {
            Product product = productService.getProductById(id);
            sendJson(exchange, 200, JsonHelper.toJson(product));
        } catch (Exception e) {
            sendError(exchange, 404, e.getMessage());
        }
    }

    private void addProduct(HttpExchange exchange) throws Exception {
        String body = readBody(exchange);
        String name = JsonHelper.getJsonStringField(body, "name");
        String priceStr = JsonHelper.getJsonStringField(body, "price");
        String stockStr = JsonHelper.getJsonStringField(body, "stock");
        String imageBase64 = JsonHelper.getJsonStringField(body, "imageBase64");

        String imageUrl = null;
        if (imageBase64 != null && imageBase64.startsWith("data:image")) {
            try {
                // Format: data:image/png;base64,iVBORw0K...
                String[] parts = imageBase64.split(",");
                if (parts.length > 1) {
                    String extension = ".png";
                    if (parts[0].contains("jpeg") || parts[0].contains("jpg")) extension = ".jpg";
                    
                    byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
                    String filename = UUID.randomUUID().toString() + extension;
                    
                    File dir = new File(System.getProperty("user.dir") + File.separator + "webapp" + File.separator + "img" + File.separator + "products");
                    if (!dir.exists()) dir.mkdirs();
                    
                    File dest = new File(dir, filename);
                    Files.write(dest.toPath(), imageBytes);
                    
                    imageUrl = "/img/products/" + filename;
                }
            } catch (Exception e) {
                System.out.println("Failed to parse image: " + e.getMessage());
            }
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            int stock = Integer.parseInt(stockStr);
            productService.addProduct(name, price, stock, imageUrl);
            sendJson(exchange, 201, JsonHelper.successJson("Product added successfully"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void updateStock(HttpExchange exchange, int id) throws Exception {
        String body = readBody(exchange);
        String countStr = JsonHelper.getJsonStringField(body, "count");
        try {
            int count = Integer.parseInt(countStr);
            productService.updateStock(id, count);
            sendJson(exchange, 200, JsonHelper.successJson("Stock updated successfully"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void deleteProduct(HttpExchange exchange, int id) throws Exception {
        try {
            productService.deleteProduct(id);
            sendJson(exchange, 200, JsonHelper.successJson("Product deleted successfully"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }
}

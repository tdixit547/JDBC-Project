package server;

import com.sun.net.httpserver.HttpExchange;
import model.Product;
import service.ProductService;
import java.math.BigDecimal;
import java.util.List;

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

        try {
            BigDecimal price = new BigDecimal(priceStr);
            int stock = Integer.parseInt(stockStr);
            productService.addProduct(name, price, stock);
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

package server;

import com.sun.net.httpserver.HttpExchange;
import model.Cart;
import service.CartService;
import java.util.List;

public class CartApiHandler extends ApiHandler {

    private final CartService cartService;

    public CartApiHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String[] parts = exchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                getCartItems(exchange, Integer.parseInt(parts[3]));
                break;
            case "POST":
                addToCart(exchange, Integer.parseInt(parts[3]));
                break;
            case "DELETE":
                if (parts.length >= 5) {
                    removeFromCart(exchange, Integer.parseInt(parts[3]), Integer.parseInt(parts[4]));
                } else {
                    clearCart(exchange, Integer.parseInt(parts[3]));
                }
                break;
            default:
                sendError(exchange, 405, "Method not allowed");
        }
    }

    private void getCartItems(HttpExchange exchange, int userId) throws Exception {
        try {
            List<Cart> items = cartService.getCartItems(userId);
            sendJson(exchange, 200, JsonHelper.toJsonArray(items, JsonHelper::toJson));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void addToCart(HttpExchange exchange, int userId) throws Exception {
        String body = readBody(exchange);
        String productIdStr = JsonHelper.getJsonStringField(body, "productId");
        String quantityStr = JsonHelper.getJsonStringField(body, "quantity");
        try {
            int productId = Integer.parseInt(productIdStr);
            int quantity = Integer.parseInt(quantityStr);
            cartService.addToCart(userId, productId, quantity);
            sendJson(exchange, 201, JsonHelper.successJson("Item added to cart"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void removeFromCart(HttpExchange exchange, int userId, int cartId) throws Exception {
        try {
            cartService.removeFromCart(userId, cartId);
            sendJson(exchange, 200, JsonHelper.successJson("Item removed from cart"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void clearCart(HttpExchange exchange, int userId) throws Exception {
        try {
            cartService.clearCart(userId);
            sendJson(exchange, 200, JsonHelper.successJson("Cart cleared"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }
}

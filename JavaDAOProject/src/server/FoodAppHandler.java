package server;

import com.sun.net.httpserver.HttpExchange;
import model.*;
import util.JwtHelper;
import util.PasswordHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all /api/* endpoints for the food delivery React frontend.
 * Routes:
 *   GET  /api/food/list          - list all foods
 *   POST /api/food/review        - add a review
 *   POST /api/user/register      - register
 *   POST /api/user/login         - login
 *   POST /api/cart/add           - add to cart (auth)
 *   POST /api/cart/remove        - remove from cart (auth)
 *   POST /api/cart/get           - get cart data (auth)
 *   POST /api/order/place        - place order (auth)
 *   POST /api/order/verify       - verify payment
 *   POST /api/order/userorders   - get user orders (auth)
 */
public class FoodAppHandler extends ApiHandler {

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        // ── Food endpoints ──
        if (path.equals("/api/food/list") && "GET".equals(method)) {
            handleFoodList(exchange);
            return;
        }
        if (path.equals("/api/food/review") && "POST".equals(method)) {
            handleAddReview(exchange);
            return;
        }

        // ── User endpoints ──
        if (path.equals("/api/user/register") && "POST".equals(method)) {
            handleRegister(exchange);
            return;
        }
        if (path.equals("/api/user/login") && "POST".equals(method)) {
            handleLogin(exchange);
            return;
        }

        // ── Cart endpoints (require auth) ──
        if (path.equals("/api/cart/add") && "POST".equals(method)) {
            handleCartAdd(exchange);
            return;
        }
        if (path.equals("/api/cart/remove") && "POST".equals(method)) {
            handleCartRemove(exchange);
            return;
        }
        if (path.equals("/api/cart/get") && "POST".equals(method)) {
            handleCartGet(exchange);
            return;
        }

        // ── Order endpoints ──
        if (path.equals("/api/order/place") && "POST".equals(method)) {
            handlePlaceOrder(exchange);
            return;
        }
        if (path.equals("/api/order/verify") && "POST".equals(method)) {
            handleVerifyOrder(exchange);
            return;
        }
        if (path.equals("/api/order/userorders") && "POST".equals(method)) {
            handleUserOrders(exchange);
            return;
        }

        sendError(exchange, 404, "Not found");
    }

    // ────────────────────────────────────────────────────────────────
    // FOOD
    // ────────────────────────────────────────────────────────────────

    private void handleFoodList(HttpExchange exchange) throws Exception {
        List<Food> foods = FoodDAO.getAllFoods();
        StringBuilder sb = new StringBuilder("{\"success\":true,\"data\":[");
        for (int i = 0; i < foods.size(); i++) {
            if (i > 0) sb.append(",");
            Food f = foods.get(i);
            // Get reviews for this food
            List<FoodReview> reviews = FoodDAO.getReviews(f.getFoodId());
            sb.append(foodToJson(f, reviews));
        }
        sb.append("]}");
        sendJson(exchange, 200, sb.toString());
    }

    private void handleAddReview(HttpExchange exchange) throws Exception {
        String body = readBody(exchange);
        String foodIdStr = JsonHelper.getJsonStringField(body, "foodId");
        String userIdStr = JsonHelper.getJsonStringField(body, "userId");
        String ratingStr = JsonHelper.getJsonStringField(body, "rating");
        String comment = JsonHelper.getJsonStringField(body, "comment");

        try {
            int foodId = Integer.parseInt(foodIdStr);
            int userId = userIdStr != null ? Integer.parseInt(userIdStr) : 0;
            double rating = Double.parseDouble(ratingStr);
            FoodDAO.addReview(foodId, userId, rating, comment);
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Review Added\"}");
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ────────────────────────────────────────────────────────────────
    // USER AUTH
    // ────────────────────────────────────────────────────────────────

    private void handleRegister(HttpExchange exchange) throws Exception {
        String body = readBody(exchange);
        String name = JsonHelper.getJsonStringField(body, "name");
        String email = JsonHelper.getJsonStringField(body, "email");
        String password = JsonHelper.getJsonStringField(body, "password");

        try {
            // Validate
            if (name == null || name.isEmpty()) {
                sendJson(exchange, 200, "{\"success\":false,\"message\":\"Name is required\"}");
                return;
            }
            if (email == null || !email.contains("@")) {
                sendJson(exchange, 200, "{\"success\":false,\"message\":\"Please enter a valid email\"}");
                return;
            }
            if (password == null || password.length() < 8) {
                sendJson(exchange, 200, "{\"success\":false,\"message\":\"Please enter a strong password\"}");
                return;
            }

            // Check if user exists
            AppUser existing = FoodDAO.findUserByEmail(email);
            if (existing != null) {
                sendJson(exchange, 200, "{\"success\":false,\"message\":\"User already exists\"}");
                return;
            }

            // Hash password
            String salt = PasswordHelper.generateSalt();
            String hash = PasswordHelper.hashPassword(password, salt);

            int userId = FoodDAO.createUser(name, email, hash, salt);
            String token = JwtHelper.createToken(userId);

            sendJson(exchange, 200, "{\"success\":true,\"token\":\"" + token + "\"}");
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleLogin(HttpExchange exchange) throws Exception {
        String body = readBody(exchange);
        String email = JsonHelper.getJsonStringField(body, "email");
        String password = JsonHelper.getJsonStringField(body, "password");

        try {
            AppUser user = FoodDAO.findUserByEmail(email);
            if (user == null) {
                sendJson(exchange, 200, "{\"success\":false,\"message\":\"User Doesn't exist\"}");
                return;
            }

            if (!PasswordHelper.verifyPassword(password, user.getPasswordHash(), user.getPasswordSalt())) {
                sendJson(exchange, 200, "{\"success\":false,\"message\":\"Invalid Credentials\"}");
                return;
            }

            String token = JwtHelper.createToken(user.getUserId());
            sendJson(exchange, 200, "{\"success\":true,\"token\":\"" + token + "\"}");
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ────────────────────────────────────────────────────────────────
    // CART
    // ────────────────────────────────────────────────────────────────

    private int authenticateUser(HttpExchange exchange) {
        String token = exchange.getRequestHeaders().getFirst("token");
        return JwtHelper.verifyToken(token);
    }

    private void handleCartAdd(HttpExchange exchange) throws Exception {
        int userId = authenticateUser(exchange);
        if (userId == -1) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"Not Authorized Login Again\"}");
            return;
        }
        String body = readBody(exchange);
        String itemIdStr = JsonHelper.getJsonStringField(body, "itemId");
        try {
            int foodId = Integer.parseInt(itemIdStr);
            FoodDAO.addToCart(userId, foodId);
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Added To Cart\"}");
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleCartRemove(HttpExchange exchange) throws Exception {
        int userId = authenticateUser(exchange);
        if (userId == -1) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"Not Authorized Login Again\"}");
            return;
        }
        String body = readBody(exchange);
        String itemIdStr = JsonHelper.getJsonStringField(body, "itemId");
        try {
            int foodId = Integer.parseInt(itemIdStr);
            FoodDAO.removeFromCart(userId, foodId);
            sendJson(exchange, 200, "{\"success\":true,\"message\":\"Removed From Cart\"}");
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleCartGet(HttpExchange exchange) throws Exception {
        int userId = authenticateUser(exchange);
        if (userId == -1) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"Not Authorized Login Again\"}");
            return;
        }
        try {
            List<int[]> items = FoodDAO.getCartData(userId);
            // Build cartData as {foodId: quantity, ...} matching frontend expectation
            StringBuilder sb = new StringBuilder("{\"success\":true,\"cartData\":{");
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(items.get(i)[0]).append("\":").append(items.get(i)[1]);
            }
            sb.append("}}");
            sendJson(exchange, 200, sb.toString());
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ────────────────────────────────────────────────────────────────
    // ORDERS
    // ────────────────────────────────────────────────────────────────

    private void handlePlaceOrder(HttpExchange exchange) throws Exception {
        int userId = authenticateUser(exchange);
        if (userId == -1) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"Not Authorized Login Again\"}");
            return;
        }
        String body = readBody(exchange);
        try {
            String amountStr = JsonHelper.getJsonStringField(body, "amount");
            BigDecimal amount = new BigDecimal(amountStr);

            // Parse address object
            // The frontend sends: address: {firstName, lastName, street, city, state, zipcode, country, phone}
            String addrFirstname = extractNestedField(body, "address", "firstName");
            String addrLastname = extractNestedField(body, "address", "lastName");
            String addrStreet = extractNestedField(body, "address", "street");
            String addrCity = extractNestedField(body, "address", "city");
            String addrState = extractNestedField(body, "address", "state");
            String addrZipcode = extractNestedField(body, "address", "zipcode");
            String addrCountry = extractNestedField(body, "address", "country");
            String addrPhone = extractNestedField(body, "address", "phone");

            FoodOrder order = new FoodOrder();
            order.setUserId(userId);
            order.setAmount(amount);
            order.setAddressFirstname(addrFirstname);
            order.setAddressLastname(addrLastname);
            order.setAddressStreet(addrStreet);
            order.setAddressCity(addrCity);
            order.setAddressState(addrState);
            order.setAddressZipcode(addrZipcode);
            order.setAddressCountry(addrCountry);
            order.setAddressPhone(addrPhone);

            // Parse items array
            List<FoodOrderItem> items = parseOrderItems(body);

            int orderId = FoodDAO.createOrder(order, items);

            // Instead of Stripe, simulate a successful payment redirect
            // The frontend redirects to this URL, which then hits /api/order/verify
            String frontendUrl = "http://localhost:5173";
            String sessionUrl = frontendUrl + "/verify?success=true&orderId=" + orderId;

            sendJson(exchange, 200, "{\"success\":true,\"session_url\":\"" + sessionUrl + "\"}");
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleVerifyOrder(HttpExchange exchange) throws Exception {
        String body = readBody(exchange);
        String orderIdStr = JsonHelper.getJsonStringField(body, "orderId");
        String successStr = JsonHelper.getJsonStringField(body, "success");

        try {
            int orderId = Integer.parseInt(orderIdStr);
            boolean success = "true".equals(successStr);
            FoodDAO.verifyOrder(orderId, success);

            if (success) {
                sendJson(exchange, 200, "{\"success\":true,\"message\":\"Paid\"}");
            } else {
                sendJson(exchange, 200, "{\"success\":false,\"message\":\"Not Paid\"}");
            }
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    private void handleUserOrders(HttpExchange exchange) throws Exception {
        int userId = authenticateUser(exchange);
        if (userId == -1) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"Not Authorized Login Again\"}");
            return;
        }
        try {
            List<FoodOrder> orders = FoodDAO.getUserOrders(userId);
            StringBuilder sb = new StringBuilder("{\"success\":true,\"data\":[");
            for (int i = 0; i < orders.size(); i++) {
                if (i > 0) sb.append(",");
                FoodOrder o = orders.get(i);
                List<FoodOrderItem> items = FoodDAO.getOrderItems(o.getOrderId());
                sb.append(orderToJson(o, items));
            }
            sb.append("]}");
            sendJson(exchange, 200, sb.toString());
        } catch (Exception e) {
            sendJson(exchange, 200, "{\"success\":false,\"message\":\"" + JsonHelper.escapeJson(e.getMessage()) + "\"}");
        }
    }

    // ────────────────────────────────────────────────────────────────
    // JSON Builders
    // ────────────────────────────────────────────────────────────────

    private String foodToJson(Food f, List<FoodReview> reviews) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"_id\":\"").append(f.getFoodId()).append("\"");
        sb.append(",\"name\":\"").append(JsonHelper.escapeJson(f.getName())).append("\"");
        sb.append(",\"description\":\"").append(JsonHelper.escapeJson(f.getDescription())).append("\"");
        sb.append(",\"price\":").append(f.getPrice());
        sb.append(",\"image\":\"").append(JsonHelper.escapeJson(f.getImage())).append("\"");
        sb.append(",\"category\":\"").append(JsonHelper.escapeJson(f.getCategory())).append("\"");
        sb.append(",\"rating\":").append(String.format("%.1f", f.getRating()));
        sb.append(",\"totalReviews\":").append(f.getTotalReviews());

        // Reviews array
        sb.append(",\"reviews\":[");
        for (int i = 0; i < reviews.size(); i++) {
            if (i > 0) sb.append(",");
            FoodReview r = reviews.get(i);
            sb.append("{\"userId\":\"").append(r.getUserId()).append("\"");
            sb.append(",\"rating\":").append(String.format("%.1f", r.getRating()));
            sb.append(",\"comment\":\"").append(JsonHelper.escapeJson(r.getComment())).append("\"");
            sb.append(",\"date\":\"").append(r.getReviewDate() != null ? r.getReviewDate().toString() : "").append("\"}");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    private String orderToJson(FoodOrder o, List<FoodOrderItem> items) {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"_id\":\"").append(o.getOrderId()).append("\"");
        sb.append(",\"userId\":\"").append(o.getUserId()).append("\"");
        sb.append(",\"amount\":").append(o.getAmount());
        sb.append(",\"status\":\"").append(JsonHelper.escapeJson(o.getStatus())).append("\"");
        sb.append(",\"date\":\"").append(o.getOrderDate() != null ? o.getOrderDate().toString() : "").append("\"");
        sb.append(",\"payment\":").append(o.isPayment());

        // Address
        sb.append(",\"address\":{");
        sb.append("\"firstName\":\"").append(JsonHelper.escapeJson(o.getAddressFirstname())).append("\"");
        sb.append(",\"lastName\":\"").append(JsonHelper.escapeJson(o.getAddressLastname())).append("\"");
        sb.append(",\"street\":\"").append(JsonHelper.escapeJson(o.getAddressStreet())).append("\"");
        sb.append(",\"city\":\"").append(JsonHelper.escapeJson(o.getAddressCity())).append("\"");
        sb.append(",\"state\":\"").append(JsonHelper.escapeJson(o.getAddressState())).append("\"");
        sb.append(",\"zipcode\":\"").append(JsonHelper.escapeJson(o.getAddressZipcode())).append("\"");
        sb.append(",\"country\":\"").append(JsonHelper.escapeJson(o.getAddressCountry())).append("\"");
        sb.append(",\"phone\":\"").append(JsonHelper.escapeJson(o.getAddressPhone())).append("\"");
        sb.append("}");

        // Items
        sb.append(",\"items\":[");
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            FoodOrderItem item = items.get(i);
            sb.append("{\"name\":\"").append(JsonHelper.escapeJson(item.getName())).append("\"");
            sb.append(",\"quantity\":").append(item.getQuantity());
            sb.append(",\"price\":").append(item.getPrice()).append("}");
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    // ────────────────────────────────────────────────────────────────
    // JSON Parsing Helpers
    // ────────────────────────────────────────────────────────────────

    /** Extract a field from a nested JSON object. e.g. extractNestedField(body, "address", "city") */
    private String extractNestedField(String json, String objectKey, String fieldKey) {
        // Find the "address":{ ... } block
        String search = "\"" + objectKey + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        int braceStart = json.indexOf('{', idx + search.length());
        if (braceStart == -1) return "";
        // Find matching close brace
        int depth = 1;
        int braceEnd = braceStart + 1;
        while (braceEnd < json.length() && depth > 0) {
            if (json.charAt(braceEnd) == '{') depth++;
            if (json.charAt(braceEnd) == '}') depth--;
            braceEnd++;
        }
        String nested = json.substring(braceStart, braceEnd);
        return JsonHelper.getJsonStringField(nested, fieldKey);
    }

    /** Parse items array from order body: "items":[{"_id":"1","name":"..","price":12,"quantity":2}, ...] */
    private List<FoodOrderItem> parseOrderItems(String json) {
        List<FoodOrderItem> items = new ArrayList<>();
        int arrStart = json.indexOf("\"items\"");
        if (arrStart == -1) return items;
        int bracketStart = json.indexOf('[', arrStart);
        if (bracketStart == -1) return items;

        // Find matching close bracket
        int depth = 1;
        int bracketEnd = bracketStart + 1;
        while (bracketEnd < json.length() && depth > 0) {
            if (json.charAt(bracketEnd) == '[') depth++;
            if (json.charAt(bracketEnd) == ']') depth--;
            bracketEnd++;
        }
        String arrContent = json.substring(bracketStart + 1, bracketEnd - 1);

        // Split by },{ pattern
        int objStart = 0;
        int d = 0;
        for (int i = 0; i < arrContent.length(); i++) {
            char c = arrContent.charAt(i);
            if (c == '{') d++;
            if (c == '}') {
                d--;
                if (d == 0) {
                    String obj = arrContent.substring(objStart, i + 1).trim();
                    if (obj.startsWith(",")) obj = obj.substring(1).trim();
                    FoodOrderItem item = new FoodOrderItem();
                    String idStr = JsonHelper.getJsonStringField(obj, "_id");
                    if (idStr != null) {
                        try { item.setFoodId(Integer.parseInt(idStr)); } catch (NumberFormatException e) {}
                    }
                    item.setName(JsonHelper.getJsonStringField(obj, "name"));
                    String priceStr = JsonHelper.getJsonStringField(obj, "price");
                    if (priceStr != null) item.setPrice(new BigDecimal(priceStr));
                    String qtyStr = JsonHelper.getJsonStringField(obj, "quantity");
                    if (qtyStr != null) item.setQuantity(Integer.parseInt(qtyStr));
                    items.add(item);
                    objStart = i + 1;
                }
            }
        }
        return items;
    }

}

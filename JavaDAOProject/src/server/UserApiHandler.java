package server;

import com.sun.net.httpserver.HttpExchange;
import model.User;
import model.Wallet;
import service.UserService;
import java.math.BigDecimal;
import java.util.List;

public class UserApiHandler extends ApiHandler {

    private final UserService userService;

    public UserApiHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String[] parts = exchange.getRequestURI().getPath().split("/");

        switch (method) {
            case "GET":
                if (parts.length >= 5 && "wallet".equals(parts[4])) {
                    getWallet(exchange, Integer.parseInt(parts[3]));
                } else if (parts.length >= 4) {
                    getById(exchange, Integer.parseInt(parts[3]));
                } else {
                    listAll(exchange);
                }
                break;
            case "POST":
                registerUser(exchange);
                break;
            case "PUT":
                if (parts.length >= 5 && "wallet".equals(parts[4])) {
                    addBalance(exchange, Integer.parseInt(parts[3]));
                } else {
                    sendError(exchange, 400, "Invalid endpoint");
                }
                break;
            default:
                sendError(exchange, 405, "Method not allowed");
        }
    }

    private void listAll(HttpExchange exchange) throws Exception {
        List<User> users = userService.getAllUsers();
        sendJson(exchange, 200, JsonHelper.toJsonArray(users, JsonHelper::toJson));
    }

    private void getById(HttpExchange exchange, int id) throws Exception {
        try {
            User user = userService.getUserById(id);
            sendJson(exchange, 200, JsonHelper.toJson(user));
        } catch (Exception e) {
            sendError(exchange, 404, e.getMessage());
        }
    }

    private void registerUser(HttpExchange exchange) throws Exception {
        String body = readBody(exchange);
        String name = JsonHelper.getJsonStringField(body, "name");
        String email = JsonHelper.getJsonStringField(body, "email");
        try {
            userService.registerUser(name, email);
            sendJson(exchange, 201, JsonHelper.successJson("User registered successfully"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void getWallet(HttpExchange exchange, int userId) throws Exception {
        try {
            Wallet wallet = userService.getWallet(userId);
            sendJson(exchange, 200, JsonHelper.toJson(wallet));
        } catch (Exception e) {
            sendError(exchange, 404, e.getMessage());
        }
    }

    private void addBalance(HttpExchange exchange, int userId) throws Exception {
        String body = readBody(exchange);
        String amountStr = JsonHelper.getJsonStringField(body, "amount");
        try {
            BigDecimal amount = new BigDecimal(amountStr);
            userService.addWalletBalance(userId, amount);
            sendJson(exchange, 200, JsonHelper.successJson("Balance added successfully"));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }
}

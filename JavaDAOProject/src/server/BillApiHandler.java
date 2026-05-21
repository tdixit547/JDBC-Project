package server;

import com.sun.net.httpserver.HttpExchange;
import model.Bill;
import model.BillItem;
import service.BillService;
import service.CheckoutService;
import java.util.List;

public class BillApiHandler extends ApiHandler {

    private final BillService billService;
    private final CheckoutService checkoutService;

    public BillApiHandler(BillService billService, CheckoutService checkoutService) {
        this.billService = billService;
        this.checkoutService = checkoutService;
    }

    @Override
    protected void handleRequest(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        // POST /api/checkout/{userId}
        if (path.startsWith("/api/checkout") && "POST".equals(method)) {
            checkout(exchange, Integer.parseInt(parts[3]));
            return;
        }

        // GET /api/bills/user/{userId}
        if ("GET".equals(method) && parts.length >= 5 && "user".equals(parts[3])) {
            getBillsByUser(exchange, Integer.parseInt(parts[4]));
            return;
        }

        // GET /api/bills/{billId}
        if ("GET".equals(method) && parts.length >= 4) {
            getBillDetails(exchange, Integer.parseInt(parts[3]));
            return;
        }

        sendError(exchange, 400, "Invalid endpoint");
    }

    private void getBillsByUser(HttpExchange exchange, int userId) throws Exception {
        try {
            List<Bill> bills = billService.getBillsByUser(userId);
            sendJson(exchange, 200, JsonHelper.toJsonArray(bills, JsonHelper::toJson));
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }

    private void getBillDetails(HttpExchange exchange, int billId) throws Exception {
        try {
            Bill bill = billService.getBillById(billId);
            List<BillItem> items = billService.getBillItems(billId);
            String json = "{\"bill\":" + JsonHelper.toJson(bill) +
                         ",\"items\":" + JsonHelper.toJsonArray(items, JsonHelper::toJson) + "}";
            sendJson(exchange, 200, json);
        } catch (Exception e) {
            sendError(exchange, 404, e.getMessage());
        }
    }

    private void checkout(HttpExchange exchange, int userId) throws Exception {
        try {
            int billId = checkoutService.checkout(userId);
            sendJson(exchange, 200, "{\"status\":\"ok\",\"billId\":" + billId + "}");
        } catch (Exception e) {
            sendError(exchange, 400, e.getMessage());
        }
    }
}

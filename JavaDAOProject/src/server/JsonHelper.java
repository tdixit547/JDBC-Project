package server;

import model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public class JsonHelper {

    public static String escapeJson(String s) {
        if (s == null) return "null";
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    public static String toJson(Product p) {
        return "{\"productId\":" + p.getProductId() +
               ",\"name\":\"" + escapeJson(p.getName()) +
               "\",\"price\":" + p.getPrice() +
               ",\"count\":" + p.getCount() + "}";
    }

    public static String toJson(User u) {
        return "{\"userId\":" + u.getUserId() +
               ",\"name\":\"" + escapeJson(u.getName()) +
               "\",\"email\":\"" + escapeJson(u.getEmail()) + "\"}";
    }

    public static String toJson(Wallet w) {
        return "{\"walletId\":" + w.getWalletId() +
               ",\"userId\":" + w.getUserId() +
               ",\"balance\":" + w.getBalance() + "}";
    }

    public static String toJson(Cart c) {
        return "{\"cartId\":" + c.getCartId() +
               ",\"userId\":" + c.getUserId() +
               ",\"productId\":" + c.getProductId() +
               ",\"quantity\":" + c.getQuantity() + "}";
    }

    public static String toJson(Bill b) {
        String dateStr = b.getBillDate() != null ? "\"" + b.getBillDate().toString() + "\"" : "null";
        return "{\"billId\":" + b.getBillId() +
               ",\"userId\":" + b.getUserId() +
               ",\"totalAmount\":" + b.getTotalAmount() +
               ",\"billDate\":" + dateStr +
               ",\"status\":\"" + escapeJson(b.getStatus()) + "\"}";
    }

    public static String toJson(BillItem bi) {
        return "{\"billItemId\":" + bi.getBillItemId() +
               ",\"billId\":" + bi.getBillId() +
               ",\"productId\":" + bi.getProductId() +
               ",\"quantity\":" + bi.getQuantity() +
               ",\"priceAtPurchase\":" + bi.getPriceAtPurchase() + "}";
    }

    public static <T> String toJsonArray(List<T> list, Function<T, String> mapper) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(mapper.apply(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String getJsonStringField(String json, String key) {
        String search = "\"" + key + "\"";
        int keyIdx = json.indexOf(search);
        if (keyIdx == -1) return null;
        int colonIdx = json.indexOf(':', keyIdx + search.length());
        if (colonIdx == -1) return null;
        String rest = json.substring(colonIdx + 1).trim();
        if (rest.startsWith("\"")) {
            int endQuote = rest.indexOf('"', 1);
            return rest.substring(1, endQuote);
        }
        int end = rest.length();
        for (int i = 0; i < rest.length(); i++) {
            char c = rest.charAt(i);
            if (c == ',' || c == '}' || c == ']') { end = i; break; }
        }
        return rest.substring(0, end).trim();
    }

    public static String successJson(String message) {
        return "{\"status\":\"ok\",\"message\":\"" + escapeJson(message) + "\"}";
    }

    public static String errorJson(String message) {
        return "{\"status\":\"error\",\"message\":\"" + escapeJson(message) + "\"}";
    }
}

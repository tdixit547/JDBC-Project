package server;

import model.*;
import util.DBConnection;
import util.JwtHelper;
import util.PasswordHelper;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * All JDBC data-access methods for the food delivery app.
 * Consolidated here for clarity — each method is a self-contained JDBC operation.
 */
public class FoodDAO {

    // ─── Foods ───────────────────────────────────────────────

    public static List<Food> getAllFoods() throws SQLException {
        List<Food> foods = new ArrayList<>();
        String sql = "SELECT * FROM foods ORDER BY food_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                foods.add(mapFood(rs));
            }
        }
        return foods;
    }

    public static List<FoodReview> getReviews(int foodId) throws SQLException {
        List<FoodReview> reviews = new ArrayList<>();
        String sql = "SELECT * FROM food_reviews WHERE food_id = ? ORDER BY review_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, foodId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FoodReview r = new FoodReview();
                    r.setReviewId(rs.getInt("review_id"));
                    r.setFoodId(rs.getInt("food_id"));
                    r.setUserId(rs.getInt("user_id"));
                    r.setRating(rs.getDouble("rating"));
                    r.setComment(rs.getString("comment"));
                    Timestamp ts = rs.getTimestamp("review_date");
                    if (ts != null) r.setReviewDate(ts.toLocalDateTime());
                    reviews.add(r);
                }
            }
        }
        return reviews;
    }

    public static void addReview(int foodId, int userId, double rating, String comment) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // Insert review
            String sql1 = "INSERT INTO food_reviews (food_id, user_id, rating, comment) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, foodId);
                ps.setInt(2, userId);
                ps.setDouble(3, rating);
                ps.setString(4, comment);
                ps.executeUpdate();
            }

            // Recalculate average rating
            String sql2 = "UPDATE foods SET total_reviews = (SELECT COUNT(*) FROM food_reviews WHERE food_id = ?), " +
                          "rating = (SELECT COALESCE(AVG(rating), 0) FROM food_reviews WHERE food_id = ?) WHERE food_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, foodId);
                ps.setInt(2, foodId);
                ps.setInt(3, foodId);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // ─── Users / Auth ────────────────────────────────────────

    public static AppUser findUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM app_users WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }
        return null;
    }

    public static AppUser findUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM app_users WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapUser(rs);
            }
        }
        return null;
    }

    public static int createUser(String name, String email, String passwordHash, String passwordSalt) throws SQLException {
        String sql = "INSERT INTO app_users (name, email, password_hash, password_salt) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, passwordSalt);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        return -1;
    }

    // ─── Cart ────────────────────────────────────────────────

    /** Get cart as {foodId: quantity} map entries */
    public static List<int[]> getCartData(int userId) throws SQLException {
        List<int[]> items = new ArrayList<>();
        String sql = "SELECT food_id, quantity FROM cart_items WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new int[]{rs.getInt("food_id"), rs.getInt("quantity")});
                }
            }
        }
        return items;
    }

    public static void addToCart(int userId, int foodId) throws SQLException {
        String sql = "INSERT INTO cart_items (user_id, food_id, quantity) VALUES (?, ?, 1) " +
                     "ON DUPLICATE KEY UPDATE quantity = quantity + 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, foodId);
            ps.executeUpdate();
        }
    }

    public static void removeFromCart(int userId, int foodId) throws SQLException {
        // Decrement, then delete if quantity <= 0
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);
            String sql1 = "UPDATE cart_items SET quantity = quantity - 1 WHERE user_id = ? AND food_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql1)) {
                ps.setInt(1, userId);
                ps.setInt(2, foodId);
                ps.executeUpdate();
            }
            String sql2 = "DELETE FROM cart_items WHERE user_id = ? AND food_id = ? AND quantity <= 0";
            try (PreparedStatement ps = conn.prepareStatement(sql2)) {
                ps.setInt(1, userId);
                ps.setInt(2, foodId);
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static void clearCart(int userId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    // ─── Orders ──────────────────────────────────────────────

    public static int createOrder(FoodOrder order, List<FoodOrderItem> items) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            String sql = "INSERT INTO orders (user_id, amount, address_firstname, address_lastname, " +
                         "address_street, address_city, address_state, address_zipcode, address_country, address_phone) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            int orderId;
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, order.getUserId());
                ps.setBigDecimal(2, order.getAmount());
                ps.setString(3, order.getAddressFirstname());
                ps.setString(4, order.getAddressLastname());
                ps.setString(5, order.getAddressStreet());
                ps.setString(6, order.getAddressCity());
                ps.setString(7, order.getAddressState());
                ps.setString(8, order.getAddressZipcode());
                ps.setString(9, order.getAddressCountry());
                ps.setString(10, order.getAddressPhone());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    orderId = keys.getInt(1);
                }
            }

            // Insert order items
            String sqlItem = "INSERT INTO order_items (order_id, food_id, name, quantity, price) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                for (FoodOrderItem item : items) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getFoodId());
                    ps.setString(3, item.getName());
                    ps.setInt(4, item.getQuantity());
                    ps.setBigDecimal(5, item.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // Clear cart
            clearCart(order.getUserId());

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static void verifyOrder(int orderId, boolean success) throws SQLException {
        if (success) {
            String sql = "UPDATE orders SET payment = TRUE WHERE order_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }
        } else {
            String sql = "DELETE FROM orders WHERE order_id = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, orderId);
                ps.executeUpdate();
            }
        }
    }

    public static List<FoodOrder> getUserOrders(int userId) throws SQLException {
        List<FoodOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        }
        return orders;
    }

    public static List<FoodOrderItem> getOrderItems(int orderId) throws SQLException {
        List<FoodOrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FoodOrderItem item = new FoodOrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setFoodId(rs.getInt("food_id"));
                    item.setName(rs.getString("name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getBigDecimal("price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    // ─── Mappers ─────────────────────────────────────────────

    private static Food mapFood(ResultSet rs) throws SQLException {
        Food f = new Food();
        f.setFoodId(rs.getInt("food_id"));
        f.setName(rs.getString("name"));
        f.setDescription(rs.getString("description"));
        f.setPrice(rs.getBigDecimal("price"));
        f.setImage(rs.getString("image"));
        f.setCategory(rs.getString("category"));
        f.setRating(rs.getDouble("rating"));
        f.setTotalReviews(rs.getInt("total_reviews"));
        return f;
    }

    private static AppUser mapUser(ResultSet rs) throws SQLException {
        AppUser u = new AppUser();
        u.setUserId(rs.getInt("user_id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setPasswordSalt(rs.getString("password_salt"));
        return u;
    }

    private static FoodOrder mapOrder(ResultSet rs) throws SQLException {
        FoodOrder o = new FoodOrder();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setAmount(rs.getBigDecimal("amount"));
        o.setAddressFirstname(rs.getString("address_firstname"));
        o.setAddressLastname(rs.getString("address_lastname"));
        o.setAddressStreet(rs.getString("address_street"));
        o.setAddressCity(rs.getString("address_city"));
        o.setAddressState(rs.getString("address_state"));
        o.setAddressZipcode(rs.getString("address_zipcode"));
        o.setAddressCountry(rs.getString("address_country"));
        o.setAddressPhone(rs.getString("address_phone"));
        o.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("order_date");
        if (ts != null) o.setOrderDate(ts.toLocalDateTime());
        o.setPayment(rs.getBoolean("payment"));
        return o;
    }
}

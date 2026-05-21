package dao.impl;

import dao.CartDAO;
import model.Cart;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO_JDBC implements CartDAO {

    private final Connection conn;

    public CartDAO_JDBC(Connection conn) {
        this.conn = conn;
    }

    private Cart mapRow(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setCartId(rs.getInt("cart_id"));
        cart.setUserId(rs.getInt("user_id"));
        cart.setProductId(rs.getInt("product_id"));
        cart.setQuantity(rs.getInt("quantity"));
        return cart;
    }

    @Override
    public List<Cart> getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM cart WHERE user_id = ?";
        List<Cart> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        }
        return items;
    }

    @Override
    public Cart findByUserAndProduct(int userId, int productId) throws SQLException {
        String sql = "SELECT * FROM cart WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void insert(Cart cart) throws SQLException {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cart.getUserId());
            ps.setInt(2, cart.getProductId());
            ps.setInt(3, cart.getQuantity());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateQuantity(int cartId, int newQuantity) throws SQLException {
        String sql = "UPDATE cart SET quantity = ? WHERE cart_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQuantity);
            ps.setInt(2, cartId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteByUserId(int userId) throws SQLException {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        }
    }

    @Override
    public void deleteItem(int cartId) throws SQLException {
        String sql = "DELETE FROM cart WHERE cart_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        }
    }
}

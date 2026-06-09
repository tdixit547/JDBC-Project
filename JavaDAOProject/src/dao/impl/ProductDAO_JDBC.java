package dao.impl;

import dao.ProductDAO;
import model.Product;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO_JDBC implements ProductDAO {

    private final Connection conn;

    public ProductDAO_JDBC(Connection conn) {
        this.conn = conn;
    }

    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setCount(rs.getInt("count"));
        product.setImageUrl(rs.getString("image_url"));
        return product;
    }

    @Override
    public Product getById(int productId) throws SQLException {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Product> getAll() throws SQLException {
        String sql = "SELECT * FROM product";
        List<Product> products = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                products.add(mapRow(rs));
            }
        }
        return products;
    }

    @Override
    public void insert(Product product) throws SQLException {
        String sql = "INSERT INTO product (name, price, count, image_url) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getCount());
            ps.setString(4, product.getImageUrl() != null ? product.getImageUrl() : "/img/placeholder.png");
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE product SET name = ?, price = ?, count = ?, image_url = ? WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, product.getName());
            ps.setBigDecimal(2, product.getPrice());
            ps.setInt(3, product.getCount());
            ps.setString(4, product.getImageUrl() != null ? product.getImageUrl() : "/img/placeholder.png");
            ps.setInt(5, product.getProductId());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateCount(int productId, int newCount) throws SQLException {
        String sql = "UPDATE product SET count = ? WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newCount);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int productId) throws SQLException {
        String sql = "DELETE FROM product WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        }
    }
}

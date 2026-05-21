package dao.impl;

import dao.BillItemDAO;
import model.BillItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BillItemDAO_JDBC implements BillItemDAO {

    private final Connection conn;

    public BillItemDAO_JDBC(Connection conn) {
        this.conn = conn;
    }

    private BillItem mapRow(ResultSet rs) throws SQLException {
        BillItem item = new BillItem();
        item.setBillItemId(rs.getInt("bill_item_id"));
        item.setBillId(rs.getInt("bill_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setPriceAtPurchase(rs.getBigDecimal("price_at_purchase"));
        return item;
    }

    @Override
    public void insertAll(List<BillItem> items) throws SQLException {
        String sql = "INSERT INTO bill_items (bill_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (BillItem item : items) {
                ps.setInt(1, item.getBillId());
                ps.setInt(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                ps.setBigDecimal(4, item.getPriceAtPurchase());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    @Override
    public List<BillItem> getByBillId(int billId) throws SQLException {
        String sql = "SELECT * FROM bill_items WHERE bill_id = ?";
        List<BillItem> items = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRow(rs));
                }
            }
        }
        return items;
    }
}

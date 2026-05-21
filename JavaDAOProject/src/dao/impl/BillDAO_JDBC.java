package dao.impl;

import dao.BillDAO;
import model.Bill;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BillDAO_JDBC implements BillDAO {

    private final Connection conn;

    public BillDAO_JDBC(Connection conn) {
        this.conn = conn;
    }

    private Bill mapRow(ResultSet rs) throws SQLException {
        Bill bill = new Bill();
        bill.setBillId(rs.getInt("bill_id"));
        bill.setUserId(rs.getInt("user_id"));
        bill.setTotalAmount(rs.getBigDecimal("total_amount"));
        bill.setBillDate(rs.getTimestamp("bill_date").toLocalDateTime());
        bill.setStatus(rs.getString("status"));
        return bill;
    }

    @Override
    public int insert(Bill bill) throws SQLException {
        String sql = "INSERT INTO bill (user_id, total_amount, status) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, bill.getUserId());
            ps.setBigDecimal(2, bill.getTotalAmount());
            ps.setString(3, bill.getStatus() != null ? bill.getStatus() : "COMPLETED");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        throw new SQLException("Failed to retrieve generated bill_id.");
    }

    @Override
    public Bill getById(int billId) throws SQLException {
        String sql = "SELECT * FROM bill WHERE bill_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, billId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Bill> getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM bill WHERE user_id = ? ORDER BY bill_date DESC";
        List<Bill> bills = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    bills.add(mapRow(rs));
                }
            }
        }
        return bills;
    }
}

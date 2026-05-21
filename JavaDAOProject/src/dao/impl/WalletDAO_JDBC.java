package dao.impl;

import dao.WalletDAO;
import model.Wallet;

import java.math.BigDecimal;
import java.sql.*;

public class WalletDAO_JDBC implements WalletDAO {

    private final Connection conn;

    public WalletDAO_JDBC(Connection conn) {
        this.conn = conn;
    }

    private Wallet mapRow(ResultSet rs) throws SQLException {
        Wallet wallet = new Wallet();
        wallet.setWalletId(rs.getInt("wallet_id"));
        wallet.setUserId(rs.getInt("user_id"));
        wallet.setBalance(rs.getBigDecimal("balance"));
        return wallet;
    }

    @Override
    public Wallet getByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM wallet WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void insert(Wallet wallet) throws SQLException {
        String sql = "INSERT INTO wallet (user_id, balance) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wallet.getUserId());
            ps.setBigDecimal(2, wallet.getBalance());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateBalance(int userId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE wallet SET balance = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }
}

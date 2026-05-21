package dao;

import model.Wallet;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface WalletDAO {
    Wallet getByUserId(int userId) throws SQLException;
    void insert(Wallet wallet) throws SQLException;
    void updateBalance(int userId, BigDecimal newBalance) throws SQLException;
}

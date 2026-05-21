package dao;

import model.Bill;
import java.sql.SQLException;
import java.util.List;

public interface BillDAO {
    int insert(Bill bill) throws SQLException;
    Bill getById(int billId) throws SQLException;
    List<Bill> getByUserId(int userId) throws SQLException;
}

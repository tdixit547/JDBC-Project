package dao;

import model.BillItem;
import java.sql.SQLException;
import java.util.List;

public interface BillItemDAO {
    void insertAll(List<BillItem> items) throws SQLException;
    List<BillItem> getByBillId(int billId) throws SQLException;
}

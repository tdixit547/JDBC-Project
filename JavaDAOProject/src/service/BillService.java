package service;

import dao.BillDAO;
import dao.BillItemDAO;
import factory.DAOFactory;
import model.Bill;
import model.BillItem;
import exceptions.*;
import java.sql.SQLException;
import java.util.List;

public class BillService {

    private final BillDAO billDAO;
    private final BillItemDAO billItemDAO;

    public BillService() throws SQLException {
        this.billDAO = DAOFactory.getBillDAO();
        this.billItemDAO = DAOFactory.getBillItemDAO();
    }

    public List<Bill> getBillsByUser(int userId) throws SQLException {
        return billDAO.getByUserId(userId);
    }

    public Bill getBillById(int billId) throws SQLException, ProductNotFoundException {
        Bill bill = billDAO.getById(billId);
        if (bill == null) {
            throw new ProductNotFoundException("Bill with ID " + billId + " not found.");
        }
        return bill;
    }

    public List<BillItem> getBillItems(int billId) throws SQLException {
        return billItemDAO.getByBillId(billId);
    }
}

package factory;

import dao.*;
import dao.impl.*;
import util.DBConnection;
import java.sql.Connection;
import java.sql.SQLException;

public class DAOFactory {
    private static Connection conn;

    // Cached DAO instances
    private static ProductDAO productDAO;
    private static UserDAO userDAO;
    private static CartDAO cartDAO;
    private static BillDAO billDAO;
    private static BillItemDAO billItemDAO;
    private static WalletDAO walletDAO;

    private static Connection getSharedConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DBConnection.getConnection();
        }
        return conn;
    }

    public static Connection getConnection() throws SQLException {
        return getSharedConnection();
    }

    public static ProductDAO getProductDAO() throws SQLException {
        if (productDAO == null) {
            productDAO = new ProductDAO_JDBC(getSharedConnection());
        }
        return productDAO;
    }

    public static UserDAO getUserDAO() throws SQLException {
        if (userDAO == null) {
            userDAO = new UserDAO_JDBC(getSharedConnection());
        }
        return userDAO;
    }

    public static CartDAO getCartDAO() throws SQLException {
        if (cartDAO == null) {
            cartDAO = new CartDAO_JDBC(getSharedConnection());
        }
        return cartDAO;
    }

    public static BillDAO getBillDAO() throws SQLException {
        if (billDAO == null) {
            billDAO = new BillDAO_JDBC(getSharedConnection());
        }
        return billDAO;
    }

    public static BillItemDAO getBillItemDAO() throws SQLException {
        if (billItemDAO == null) {
            billItemDAO = new BillItemDAO_JDBC(getSharedConnection());
        }
        return billItemDAO;
    }

    public static WalletDAO getWalletDAO() throws SQLException {
        if (walletDAO == null) {
            walletDAO = new WalletDAO_JDBC(getSharedConnection());
        }
        return walletDAO;
    }
}

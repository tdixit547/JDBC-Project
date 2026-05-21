package dao;

import model.Product;
import java.sql.SQLException;
import java.util.List;

public interface ProductDAO {
    Product getById(int productId) throws SQLException;
    List<Product> getAll() throws SQLException;
    void insert(Product product) throws SQLException;
    void update(Product product) throws SQLException;
    void updateCount(int productId, int newCount) throws SQLException;
    void delete(int productId) throws SQLException;
}

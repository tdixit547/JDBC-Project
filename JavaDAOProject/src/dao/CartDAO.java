package dao;

import model.Cart;
import java.sql.SQLException;
import java.util.List;

public interface CartDAO {
    List<Cart> getByUserId(int userId) throws SQLException;
    Cart findByUserAndProduct(int userId, int productId) throws SQLException;
    void insert(Cart cart) throws SQLException;
    void updateQuantity(int cartId, int newQuantity) throws SQLException;
    void deleteByUserId(int userId) throws SQLException;
    void deleteItem(int cartId) throws SQLException;
}

package dao;

import model.User;
import java.sql.SQLException;
import java.util.List;

public interface UserDAO {
    User getById(int userId) throws SQLException;
    List<User> getAll() throws SQLException;
    int insert(User user) throws SQLException;
    void update(User user) throws SQLException;
    void delete(int userId) throws SQLException;
}

package service;

import dao.UserDAO;
import dao.WalletDAO;
import factory.DAOFactory;
import model.User;
import model.Wallet;
import exceptions.*;
import util.TransactionManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO;
    private final WalletDAO walletDAO;

    public UserService() throws SQLException {
        this.userDAO = DAOFactory.getUserDAO();
        this.walletDAO = DAOFactory.getWalletDAO();
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAll();
    }

    public User getUserById(int userId) throws SQLException, UserNotFoundException {
        User user = userDAO.getById(userId);
        if (user == null) {
            throw new UserNotFoundException("User with ID " + userId + " not found.");
        }
        return user;
    }

    /**
     * Registers a new user AND creates their wallet atomically.
     */
    public void registerUser(String name, String email) throws Exception {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("User name cannot be empty.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException("Email cannot be empty.");
        }

        Connection conn = DAOFactory.getConnection();
        TransactionManager.executeInTransaction(conn, () -> {
            User user = new User(name.trim(), email.trim());
            int userId = userDAO.insert(user);
            walletDAO.insert(new Wallet(userId, BigDecimal.ZERO));
        });
    }

    public void updateUser(User user) throws SQLException, InvalidInputException {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            throw new InvalidInputException("User name cannot be empty.");
        }
        userDAO.update(user);
    }

    public void deleteUser(int userId) throws SQLException {
        userDAO.delete(userId);
    }

    public Wallet getWallet(int userId) throws SQLException, UserNotFoundException {
        Wallet wallet = walletDAO.getByUserId(userId);
        if (wallet == null) {
            throw new UserNotFoundException("Wallet not found for user ID " + userId + ".");
        }
        return wallet;
    }

    public void addWalletBalance(int userId, BigDecimal amount) throws Exception {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidInputException("Amount must be greater than zero.");
        }
        Wallet wallet = walletDAO.getByUserId(userId);
        if (wallet == null) {
            throw new UserNotFoundException("Wallet not found for user ID " + userId + ".");
        }
        BigDecimal newBalance = wallet.getBalance().add(amount);
        walletDAO.updateBalance(userId, newBalance);
    }
}

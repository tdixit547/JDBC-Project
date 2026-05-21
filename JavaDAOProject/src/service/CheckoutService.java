package service;

import dao.*;
import factory.DAOFactory;
import model.*;
import exceptions.*;
import util.TransactionManager;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CheckoutService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;
    private final WalletDAO walletDAO;
    private final BillDAO billDAO;
    private final BillItemDAO billItemDAO;

    public CheckoutService() throws SQLException {
        this.cartDAO = DAOFactory.getCartDAO();
        this.productDAO = DAOFactory.getProductDAO();
        this.walletDAO = DAOFactory.getWalletDAO();
        this.billDAO = DAOFactory.getBillDAO();
        this.billItemDAO = DAOFactory.getBillItemDAO();
    }

    /**
     * Performs checkout for a user:
     * 1. Validates cart is not empty
     * 2. Calculates total from current product prices
     * 3. Validates wallet balance
     * 4. Deducts wallet
     * 5. Creates bill + bill items
     * 6. Clears cart
     * All inside a single transaction.
     * Returns the generated bill ID.
     */
    public int checkout(int userId) throws Exception {
        if (userId <= 0) {
            throw new InvalidInputException("User ID must be a positive number.");
        }
        Connection conn = DAOFactory.getConnection();

        return TransactionManager.executeInTransaction(conn, () -> {
            // 1. Get cart items (inside transaction)
            List<Cart> cartItems = cartDAO.getByUserId(userId);
            if (cartItems.isEmpty()) {
                throw new EmptyCartException("Cart is empty for user ID " + userId + ". Nothing to checkout.");
            }

            // 2. Calculate total and build bill items list
            BigDecimal totalAmount = BigDecimal.ZERO;
            List<BillItem> billItems = new ArrayList<>();

            for (Cart cartItem : cartItems) {
                Product product = productDAO.getById(cartItem.getProductId());
                if (product == null) {
                    throw new ProductNotFoundException("Product ID " + cartItem.getProductId() + " no longer exists.");
                }
                BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                totalAmount = totalAmount.add(lineTotal);

                // Bill item with price snapshot
                billItems.add(new BillItem(0, cartItem.getProductId(), cartItem.getQuantity(), product.getPrice()));
            }

            // 3. Validate wallet balance
            Wallet wallet = walletDAO.getByUserId(userId);
            if (wallet == null) {
                throw new InsufficientBalanceException("No wallet found for user ID " + userId + ".");
            }
            if (wallet.getBalance().compareTo(totalAmount) < 0) {
                throw new InsufficientBalanceException(
                    "Insufficient wallet balance. Required: " + totalAmount + ", Available: " + wallet.getBalance());
            }

            // 4. Deduct wallet
            BigDecimal newBalance = wallet.getBalance().subtract(totalAmount);
            walletDAO.updateBalance(userId, newBalance);

            // 5. Create bill
            Bill bill = new Bill(userId, totalAmount);
            int billId = billDAO.insert(bill);

            // 6. Set billId on all bill items and insert
            for (BillItem item : billItems) {
                item.setBillId(billId);
            }
            billItemDAO.insertAll(billItems);

            // 7. Clear cart (stock was already decremented during addToCart)
            cartDAO.deleteByUserId(userId);

            return billId;
        });
    }
}

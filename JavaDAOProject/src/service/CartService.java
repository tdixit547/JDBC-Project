package service;

import dao.CartDAO;
import dao.ProductDAO;
import factory.DAOFactory;
import model.Cart;
import model.Product;
import exceptions.*;
import util.TransactionManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CartService {

    private final CartDAO cartDAO;
    private final ProductDAO productDAO;

    public CartService() throws SQLException {
        this.cartDAO = DAOFactory.getCartDAO();
        this.productDAO = DAOFactory.getProductDAO();
    }

    public List<Cart> getCartItems(int userId) throws SQLException {
        return cartDAO.getByUserId(userId);
    }

    /**
     * Adds a product to the user's cart.
     * If the product is already in the cart, merges quantities.
     * Decrements product stock atomically.
     */
    public void addToCart(int userId, int productId, int quantity) throws Exception {
        if (quantity <= 0) {
            throw new InvalidInputException("Quantity must be greater than zero.");
        }

        Connection conn = DAOFactory.getConnection();
        TransactionManager.executeInTransaction(conn, () -> {
            // Validate product exists and has enough stock
            Product product = productDAO.getById(productId);
            if (product == null) {
                throw new ProductNotFoundException("Product with ID " + productId + " not found.");
            }
            if (product.getCount() < quantity) {
                throw new InsufficientStockException(
                    "Insufficient stock for '" + product.getName() + "'. Available: " + product.getCount() + ", Requested: " + quantity);
            }

            // Check if product already in cart — merge if so
            Cart existingCartItem = cartDAO.findByUserAndProduct(userId, productId);
            if (existingCartItem != null) {
                int newQty = existingCartItem.getQuantity() + quantity;
                cartDAO.updateQuantity(existingCartItem.getCartId(), newQty);
            } else {
                cartDAO.insert(new Cart(userId, productId, quantity));
            }

            // Decrement stock
            productDAO.updateCount(productId, product.getCount() - quantity);
        });
    }

    /**
     * Removes an item from cart and RESTORES the product stock.
     * Requires userId to look up the cart item details.
     */
    public void removeFromCart(int userId, int cartId) throws Exception {
        Connection conn = DAOFactory.getConnection();
        TransactionManager.executeInTransaction(conn, () -> {
            // Find the cart item to restore stock
            List<Cart> cartItems = cartDAO.getByUserId(userId);
            Cart targetItem = null;
            for (Cart item : cartItems) {
                if (item.getCartId() == cartId) {
                    targetItem = item;
                    break;
                }
            }
            if (targetItem == null) {
                throw new InvalidInputException("Cart item with ID " + cartId + " not found for user " + userId + ".");
            }

            // Restore product stock
            Product product = productDAO.getById(targetItem.getProductId());
            if (product != null) {
                productDAO.updateCount(product.getProductId(), product.getCount() + targetItem.getQuantity());
            }

            // Delete cart item
            cartDAO.deleteItem(cartId);
        });
    }

    /**
     * Clears all items from the user's cart and RESTORES all product stock.
     */
    public void clearCart(int userId) throws Exception {
        Connection conn = DAOFactory.getConnection();
        TransactionManager.executeInTransaction(conn, () -> {
            List<Cart> cartItems = cartDAO.getByUserId(userId);

            // Restore stock for each cart item
            for (Cart item : cartItems) {
                Product product = productDAO.getById(item.getProductId());
                if (product != null) {
                    productDAO.updateCount(product.getProductId(), product.getCount() + item.getQuantity());
                }
            }

            // Clear the cart
            cartDAO.deleteByUserId(userId);
        });
    }
}

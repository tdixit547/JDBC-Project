package controller;

import model.Cart;
import model.Product;
import service.CartService;
import service.ProductService;
import java.util.List;
import java.util.Scanner;

public class CartController {

    private final Scanner scanner;
    private final CartService cartService;
    private final ProductService productService;

    public CartController(Scanner scanner, CartService cartService, ProductService productService) {
        this.scanner = scanner;
        this.cartService = cartService;
        this.productService = productService;
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printHeader("CART OPERATIONS");
            System.out.println("  1. View Cart");
            System.out.println("  2. Add to Cart");
            System.out.println("  3. Remove Item from Cart");
            System.out.println("  4. Clear Cart");
            System.out.println("  0. Back to Main Menu");
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Enter your choice: ");
            switch (choice) {
                case 1: viewCart(); break;
                case 2: addToCart(); break;
                case 3: removeFromCart(); break;
                case 4: clearCart(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please try again.");
            }
        }
    }

    private void viewCart() {
        ConsoleHelper.printSubHeader("VIEW CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");
        try {
            List<Cart> items = cartService.getCartItems(userId);
            if (items.isEmpty()) {
                ConsoleHelper.printInfo("Cart is empty for User ID: " + userId);
                return;
            }
            System.out.println();
            System.out.printf("  %-8s %-10s %-12s %-10s%n", "CartID", "UserID", "ProductID", "Quantity");
            ConsoleHelper.printDivider();
            for (Cart c : items) {
                System.out.printf("  %-8d %-10d %-12d %-10d%n",
                    c.getCartId(), c.getUserId(), c.getProductId(), c.getQuantity());
            }
            ConsoleHelper.printDivider();
            ConsoleHelper.printInfo("Total items in cart: " + items.size());
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to fetch cart: " + e.getMessage());
        }
    }

    private void addToCart() {
        ConsoleHelper.printSubHeader("ADD TO CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");

        // Show available products first
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                ConsoleHelper.printInfo("No products available.");
                return;
            }
            System.out.println();
            System.out.println("  Available Products:");
            System.out.printf("  %-6s %-25s %-12s %-8s%n", "ID", "Name", "Price", "Stock");
            ConsoleHelper.printDivider();
            for (Product p : products) {
                System.out.printf("  %-6d %-25s %-12s %-8d%n",
                    p.getProductId(), p.getName(), p.getPrice(), p.getCount());
            }
            ConsoleHelper.printDivider();
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to fetch products: " + e.getMessage());
            return;
        }

        int productId = ConsoleHelper.readInt(scanner, "Enter Product ID: ");
        int quantity = ConsoleHelper.readInt(scanner, "Enter Quantity: ");
        try {
            cartService.addToCart(userId, productId, quantity);
            ConsoleHelper.printSuccess("Added Product ID " + productId + " (qty: " + quantity + ") to cart.");
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to add to cart: " + e.getMessage());
        }
    }

    private void removeFromCart() {
        ConsoleHelper.printSubHeader("REMOVE FROM CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");

        // Show current cart first
        try {
            List<Cart> items = cartService.getCartItems(userId);
            if (items.isEmpty()) {
                ConsoleHelper.printInfo("Cart is empty for User ID: " + userId);
                return;
            }
            System.out.println();
            System.out.printf("  %-8s %-12s %-10s%n", "CartID", "ProductID", "Quantity");
            ConsoleHelper.printDivider();
            for (Cart c : items) {
                System.out.printf("  %-8d %-12d %-10d%n",
                    c.getCartId(), c.getProductId(), c.getQuantity());
            }
            ConsoleHelper.printDivider();
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to fetch cart: " + e.getMessage());
            return;
        }

        int cartId = ConsoleHelper.readInt(scanner, "Enter Cart ID to remove: ");
        try {
            cartService.removeFromCart(userId, cartId);
            ConsoleHelper.printSuccess("Removed Cart ID " + cartId + " from cart.");
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to remove from cart: " + e.getMessage());
        }
    }

    private void clearCart() {
        ConsoleHelper.printSubHeader("CLEAR CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");
        try {
            cartService.clearCart(userId);
            ConsoleHelper.printSuccess("Cart cleared for User ID: " + userId);
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to clear cart: " + e.getMessage());
        }
    }
}

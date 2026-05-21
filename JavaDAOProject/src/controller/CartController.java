package controller;

import model.Cart;
import model.Product;
import service.CartService;
import service.ProductService;

import java.util.ArrayList;
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
            System.out.println();
            ConsoleHelper.printMenuItem(1, "View Cart");
            ConsoleHelper.printMenuItem(2, "Add to Cart");
            ConsoleHelper.printMenuItem(3, "Remove Item from Cart");
            ConsoleHelper.printMenuItem(4, "Clear Entire Cart");
            System.out.println();
            ConsoleHelper.printMenuBack();
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Choose an option");
            switch (choice) {
                case 1: viewCart(); break;
                case 2: addToCart(); break;
                case 3: removeFromCart(); break;
                case 4: clearCart(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please select 0\u20134.");
            }
        }
    }

    private void viewCart() {
        ConsoleHelper.printSubHeader("YOUR CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            List<Cart> items = cartService.getCartItems(userId);
            if (items.isEmpty()) {
                ConsoleHelper.printWarning("Cart is empty for User #" + userId + ".");
            } else {
                String[] headers = {"Cart ID", "Product ID", "Quantity"};
                List<String[]> rows = new ArrayList<>();
                int totalItems = 0;
                for (Cart c : items) {
                    rows.add(new String[]{
                        String.valueOf(c.getCartId()),
                        "Product #" + c.getProductId(),
                        "x" + c.getQuantity()
                    });
                    totalItems += c.getQuantity();
                }
                ConsoleHelper.printTable(headers, rows);
                ConsoleHelper.printInfo(items.size() + " item(s) in cart, " + totalItems + " total units.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load cart: " + e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void addToCart() {
        ConsoleHelper.printSubHeader("ADD TO CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            // Show available products
            ConsoleHelper.printInfo("Available products:");
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                ConsoleHelper.printWarning("No products available in the catalog.");
                ConsoleHelper.pressEnterToContinue(scanner);
                return;
            }

            String[] headers = {"ID", "Product Name", "Price", "Available"};
            List<String[]> rows = new ArrayList<>();
            for (Product p : products) {
                String stockDisplay;
                if (p.getCount() == 0) {
                    stockDisplay = ConsoleHelper.RED + "OUT OF STOCK" + ConsoleHelper.RESET;
                } else if (p.getCount() < 5) {
                    stockDisplay = ConsoleHelper.YELLOW + p.getCount() + " left" + ConsoleHelper.RESET;
                } else {
                    stockDisplay = ConsoleHelper.GREEN + p.getCount() + " available" + ConsoleHelper.RESET;
                }
                rows.add(new String[]{
                    String.valueOf(p.getProductId()),
                    p.getName(),
                    ConsoleHelper.formatCurrency(p.getPrice()),
                    stockDisplay
                });
            }
            ConsoleHelper.printTable(headers, rows);

            int productId = ConsoleHelper.readInt(scanner, "Enter Product ID");
            int quantity = ConsoleHelper.readInt(scanner, "Enter quantity");

            cartService.addToCart(userId, productId, quantity);
            System.out.println();
            ConsoleHelper.printSuccess("Added " + quantity + " unit(s) of Product #" + productId + " to cart!");
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void removeFromCart() {
        ConsoleHelper.printSubHeader("REMOVE FROM CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            // Show current cart
            List<Cart> items = cartService.getCartItems(userId);
            if (items.isEmpty()) {
                ConsoleHelper.printWarning("Cart is empty. Nothing to remove.");
                ConsoleHelper.pressEnterToContinue(scanner);
                return;
            }

            String[] headers = {"Cart ID", "Product ID", "Quantity"};
            List<String[]> rows = new ArrayList<>();
            for (Cart c : items) {
                rows.add(new String[]{
                    String.valueOf(c.getCartId()),
                    "Product #" + c.getProductId(),
                    "x" + c.getQuantity()
                });
            }
            ConsoleHelper.printTable(headers, rows);

            int cartId = ConsoleHelper.readInt(scanner, "Enter Cart ID to remove");
            cartService.removeFromCart(userId, cartId);
            System.out.println();
            ConsoleHelper.printSuccess("Item removed from cart. Product stock has been restored.");
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void clearCart() {
        ConsoleHelper.printSubHeader("CLEAR CART");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            List<Cart> items = cartService.getCartItems(userId);
            if (items.isEmpty()) {
                ConsoleHelper.printWarning("Cart is already empty.");
                ConsoleHelper.pressEnterToContinue(scanner);
                return;
            }

            ConsoleHelper.printInfo("Cart has " + items.size() + " item(s).");
            ConsoleHelper.printWarning("This will remove all items and restore all product stock.");
            boolean confirm = ConsoleHelper.readConfirmation(scanner, "Clear entire cart?");

            if (confirm) {
                cartService.clearCart(userId);
                ConsoleHelper.printSuccess("Cart cleared! All product stock has been restored.");
            } else {
                ConsoleHelper.printInfo("Cart clear cancelled.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }
}

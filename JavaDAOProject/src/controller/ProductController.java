package controller;

import java.math.BigDecimal;
import model.Product;
import service.ProductService;
import java.util.List;
import java.util.Scanner;

public class ProductController {

    private final Scanner scanner;
    private final ProductService productService;

    public ProductController(Scanner scanner, ProductService productService) {
        this.scanner = scanner;
        this.productService = productService;
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printHeader("PRODUCT MANAGEMENT");
            System.out.println("  1. View All Products");
            System.out.println("  2. View Product by ID");
            System.out.println("  3. Add New Product");
            System.out.println("  4. Update Product Stock");
            System.out.println("  5. Delete Product");
            System.out.println("  0. Back to Main Menu");
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Enter your choice: ");
            switch (choice) {
                case 1: viewAllProducts(); break;
                case 2: viewProductById(); break;
                case 3: addProduct(); break;
                case 4: updateStock(); break;
                case 5: deleteProduct(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllProducts() {
        ConsoleHelper.printSubHeader("ALL PRODUCTS");
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                ConsoleHelper.printInfo("No products found.");
                return;
            }
            printProductTable(products);
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to fetch products: " + e.getMessage());
        }
    }

    private void viewProductById() {
        ConsoleHelper.printSubHeader("VIEW PRODUCT BY ID");
        int id = ConsoleHelper.readInt(scanner, "Enter Product ID: ");
        try {
            Product product = productService.getProductById(id);
            printProductTable(List.of(product));
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
    }

    private void addProduct() {
        ConsoleHelper.printSubHeader("ADD NEW PRODUCT");
        String name = ConsoleHelper.readString(scanner, "Enter Product Name: ");
        BigDecimal price = ConsoleHelper.readBigDecimal(scanner, "Enter Price: ");
        int stock = ConsoleHelper.readInt(scanner, "Enter Stock Quantity: ");
        try {
            productService.addProduct(name, price, stock);
            ConsoleHelper.printSuccess("Product '" + name + "' added successfully!");
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to add product: " + e.getMessage());
        }
    }

    private void updateStock() {
        ConsoleHelper.printSubHeader("UPDATE PRODUCT STOCK");
        int id = ConsoleHelper.readInt(scanner, "Enter Product ID: ");
        int newStock = ConsoleHelper.readInt(scanner, "Enter New Stock Quantity: ");
        try {
            productService.updateStock(id, newStock);
            ConsoleHelper.printSuccess("Stock updated successfully for Product ID: " + id);
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to update stock: " + e.getMessage());
        }
    }

    private void deleteProduct() {
        ConsoleHelper.printSubHeader("DELETE PRODUCT");
        int id = ConsoleHelper.readInt(scanner, "Enter Product ID to delete: ");
        try {
            productService.deleteProduct(id);
            ConsoleHelper.printSuccess("Product ID " + id + " deleted successfully.");
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to delete product: " + e.getMessage());
        }
    }

    private void printProductTable(List<Product> products) {
        System.out.println();
        System.out.printf("  %-6s %-25s %-12s %-8s%n", "ID", "Name", "Price", "Stock");
        ConsoleHelper.printDivider();
        for (Product p : products) {
            System.out.printf("  %-6d %-25s %-12s %-8d%n",
                p.getProductId(), p.getName(), p.getPrice(), p.getCount());
        }
        ConsoleHelper.printDivider();
        ConsoleHelper.printInfo("Total products: " + products.size());
    }
}

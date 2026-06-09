package controller;

import model.Product;
import service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
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
            System.out.println();
            ConsoleHelper.printMenuItem(1, "View All Products");
            ConsoleHelper.printMenuItem(2, "View Product Details");
            ConsoleHelper.printMenuItem(3, "Add New Product");
            ConsoleHelper.printMenuItem(4, "Update Stock");
            ConsoleHelper.printMenuItem(5, "Delete Product");
            System.out.println();
            ConsoleHelper.printMenuBack();
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Choose an option");
            switch (choice) {
                case 1: viewAllProducts(); break;
                case 2: viewProductById(); break;
                case 3: addProduct(); break;
                case 4: updateStock(); break;
                case 5: deleteProduct(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please select 0\u20135.");
            }
        }
    }

    private void viewAllProducts() {
        ConsoleHelper.printSubHeader("ALL PRODUCTS");
        try {
            List<Product> products = productService.getAllProducts();
            if (products.isEmpty()) {
                ConsoleHelper.printWarning("No products found in the catalog.");
            } else {
                String[] headers = {"ID", "Product Name", "Price", "Stock"};
                List<String[]> rows = new ArrayList<>();
                for (Product p : products) {
                    String stockDisplay = p.getCount() + " units";
                    if (p.getCount() == 0) {
                        stockDisplay = ConsoleHelper.BOLD + ConsoleHelper.RED + "OUT OF STOCK" + ConsoleHelper.RESET;
                    } else if (p.getCount() < 5) {
                        stockDisplay = ConsoleHelper.YELLOW + p.getCount() + " units (LOW)" + ConsoleHelper.RESET;
                    }
                    rows.add(new String[]{
                        String.valueOf(p.getProductId()),
                        p.getName(),
                        ConsoleHelper.formatCurrency(p.getPrice()),
                        stockDisplay
                    });
                }
                ConsoleHelper.printTable(headers, rows);
                ConsoleHelper.printInfo(products.size() + " product(s) found.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load products: " + e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void viewProductById() {
        ConsoleHelper.printSubHeader("PRODUCT DETAILS");
        int id = ConsoleHelper.readInt(scanner, "Enter Product ID");
        try {
            Product p = productService.getProductById(id);
            System.out.println();
            ConsoleHelper.printKeyValue("Product ID", String.valueOf(p.getProductId()));
            ConsoleHelper.printKeyValue("Name", p.getName());
            ConsoleHelper.printKeyValue("Price", ConsoleHelper.formatCurrency(p.getPrice()));

            String stockStatus;
            if (p.getCount() == 0) {
                stockStatus = ConsoleHelper.BOLD + ConsoleHelper.RED + "OUT OF STOCK" + ConsoleHelper.RESET;
            } else if (p.getCount() < 5) {
                stockStatus = ConsoleHelper.YELLOW + p.getCount() + " units - LOW STOCK" + ConsoleHelper.RESET;
            } else {
                stockStatus = ConsoleHelper.GREEN + p.getCount() + " units - In Stock" + ConsoleHelper.RESET;
            }
            ConsoleHelper.printKeyValue("Stock", stockStatus);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void addProduct() {
        ConsoleHelper.printSubHeader("ADD NEW PRODUCT");
        String name = ConsoleHelper.readString(scanner, "Product name");
        BigDecimal price = ConsoleHelper.readBigDecimal(scanner, "Price");
        int stock = ConsoleHelper.readInt(scanner, "Initial stock quantity");

        try {
            productService.addProduct(name, price, stock, null);
            System.out.println();
            ConsoleHelper.printSuccess("Product '" + name + "' added successfully!");
            ConsoleHelper.printKeyValue("Price", ConsoleHelper.formatCurrency(price));
            ConsoleHelper.printKeyValue("Stock", stock + " units");
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void updateStock() {
        ConsoleHelper.printSubHeader("UPDATE STOCK");
        try {
            // Show current products first
            List<Product> products = productService.getAllProducts();
            if (!products.isEmpty()) {
                String[] headers = {"ID", "Product Name", "Current Stock"};
                List<String[]> rows = new ArrayList<>();
                for (Product p : products) {
                    rows.add(new String[]{
                        String.valueOf(p.getProductId()),
                        p.getName(),
                        p.getCount() + " units"
                    });
                }
                ConsoleHelper.printTable(headers, rows);
            }

            int id = ConsoleHelper.readInt(scanner, "Enter Product ID to update");
            int newCount = ConsoleHelper.readInt(scanner, "Enter new stock count");
            productService.updateStock(id, newCount);
            ConsoleHelper.printSuccess("Stock updated to " + newCount + " units.");
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void deleteProduct() {
        ConsoleHelper.printSubHeader("DELETE PRODUCT");
        int id = ConsoleHelper.readInt(scanner, "Enter Product ID to delete");

        try {
            Product p = productService.getProductById(id);
            ConsoleHelper.printWarning("You are about to delete: " + p.getName());
            boolean confirm = ConsoleHelper.readConfirmation(scanner, "Are you sure?");

            if (confirm) {
                productService.deleteProduct(id);
                ConsoleHelper.printSuccess("Product '" + p.getName() + "' deleted.");
            } else {
                ConsoleHelper.printInfo("Deletion cancelled.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }
}

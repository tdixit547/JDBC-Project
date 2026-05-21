package controller;

import service.*;
import java.util.Scanner;

public class MenuController {

    private final Scanner scanner;
    private final ProductController productController;
    private final UserController userController;
    private final CartController cartController;
    private final BillController billController;
    private final CheckoutService checkoutService;
    private final BillService billService;

    public MenuController(Scanner scanner, ProductService productService, UserService userService,
                          CartService cartService, CheckoutService checkoutService, BillService billService) {
        this.scanner = scanner;
        this.checkoutService = checkoutService;
        this.billService = billService;
        this.productController = new ProductController(scanner, productService);
        this.userController = new UserController(scanner, userService);
        this.cartController = new CartController(scanner, cartService, productService);
        this.billController = new BillController(scanner, billService);
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = ConsoleHelper.readInt(scanner, "Enter your choice: ");
            switch (choice) {
                case 1: productController.showMenu(); break;
                case 2: userController.showMenu(); break;
                case 3: cartController.showMenu(); break;
                case 4: handleCheckout(); break;
                case 5: billController.showMenu(); break;
                case 0:
                    running = false;
                    ConsoleHelper.printInfo("Thank you for using Java Shop System. Goodbye!");
                    break;
                default:
                    ConsoleHelper.printError("Invalid choice. Please try again.");
            }
        }
    }

    private void printMainMenu() {
        ConsoleHelper.printHeader("WELCOME TO JAVA SHOP SYSTEM");
        System.out.println("  1. Product Management");
        System.out.println("  2. User Management");
        System.out.println("  3. Cart Operations");
        System.out.println("  4. Checkout");
        System.out.println("  5. View Bills");
        System.out.println("  0. Exit");
        ConsoleHelper.printDivider();
    }

    private void handleCheckout() {
        ConsoleHelper.printSubHeader("CHECKOUT");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");
        try {
            int billId = checkoutService.checkout(userId);
            ConsoleHelper.printSuccess("Checkout successful! Bill ID: " + billId);

            // Display the bill details
            try {
                var bill = billService.getBillById(billId);
                var items = billService.getBillItems(billId);
                System.out.println("  Total Amount: " + bill.getTotalAmount());
                System.out.println("  Date: " + bill.getBillDate());
                System.out.println("  Status: " + bill.getStatus());
                if (!items.isEmpty()) {
                    System.out.println("  Items:");
                    System.out.printf("    %-10s %-12s %-15s%n", "ProductID", "Quantity", "Price");
                    for (var item : items) {
                        System.out.printf("    %-10d %-12d %-15s%n",
                            item.getProductId(), item.getQuantity(), item.getPriceAtPurchase());
                    }
                }
            } catch (Exception e) {
                // Bill was created but display failed — not critical
                ConsoleHelper.printInfo("Bill created but could not display details: " + e.getMessage());
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
    }
}

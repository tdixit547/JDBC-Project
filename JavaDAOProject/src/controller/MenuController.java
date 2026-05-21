package controller;

import service.*;
import model.*;
import java.util.*;
import java.math.BigDecimal;

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
        ConsoleHelper.enableWindowsAnsi();
        ConsoleHelper.printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = ConsoleHelper.readInt(scanner, "Choose an option");
            switch (choice) {
                case 1: productController.showMenu(); break;
                case 2: userController.showMenu(); break;
                case 3: cartController.showMenu(); break;
                case 4: handleCheckout(); break;
                case 5: billController.showMenu(); break;
                case 0:
                    running = false;
                    System.out.println();
                    ConsoleHelper.printDivider();
                    ConsoleHelper.printInfo("Goodbye!");
                    ConsoleHelper.printDivider();
                    System.out.println();
                    break;
                default:
                    ConsoleHelper.printError("Invalid choice. Please select 0\u20135.");
            }
        }
    }

    private void printMainMenu() {
        ConsoleHelper.printHeader("MAIN MENU");
        System.out.println();
        ConsoleHelper.printMenuItem(1, "Product Management");
        ConsoleHelper.printMenuItem(2, "User Management");
        ConsoleHelper.printMenuItem(3, "Cart Operations");
        ConsoleHelper.printMenuItem(4, "Checkout");
        ConsoleHelper.printMenuItem(5, "View Bills & Orders");
        System.out.println();
        ConsoleHelper.printMenuExit();
        ConsoleHelper.printDivider();
    }

    private void handleCheckout() {
        ConsoleHelper.printHeader("CHECKOUT");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            System.out.println();
            ConsoleHelper.printInfo("Processing checkout...");
            int billId = checkoutService.checkout(userId);
            System.out.println();
            ConsoleHelper.printSuccess("Checkout successful!");

            // Display receipt
            try {
                Bill bill = billService.getBillById(billId);
                List<BillItem> items = billService.getBillItems(billId);

                List<String[]> receiptItems = new ArrayList<>();
                for (BillItem item : items) {
                    BigDecimal lineTotal = item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity()));
                    receiptItems.add(new String[]{
                        "Product #" + item.getProductId(),
                        "x" + item.getQuantity(),
                        ConsoleHelper.formatCurrency(item.getPriceAtPurchase()),
                        ConsoleHelper.formatCurrency(lineTotal)
                    });
                }

                ConsoleHelper.printReceipt(
                    "INVOICE #" + billId,
                    receiptItems,
                    "TOTAL",
                    ConsoleHelper.formatCurrency(bill.getTotalAmount())
                );

                ConsoleHelper.printKeyValue("Status", bill.getStatus());
                ConsoleHelper.printKeyValue("Date", bill.getBillDate() != null
                    ? bill.getBillDate().toString() : "N/A");

            } catch (Exception e) {
                ConsoleHelper.printWarning("Bill created but could not display receipt: " + e.getMessage());
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }
}

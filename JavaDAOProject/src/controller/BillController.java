package controller;

import model.Bill;
import model.BillItem;
import service.BillService;
import java.util.List;
import java.util.Scanner;

public class BillController {

    private final Scanner scanner;
    private final BillService billService;

    public BillController(Scanner scanner, BillService billService) {
        this.scanner = scanner;
        this.billService = billService;
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printHeader("BILL MANAGEMENT");
            System.out.println("  1. View Bills by User");
            System.out.println("  2. View Bill Details");
            System.out.println("  0. Back to Main Menu");
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Enter your choice: ");
            switch (choice) {
                case 1: viewBillsByUser(); break;
                case 2: viewBillDetails(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please try again.");
            }
        }
    }

    private void viewBillsByUser() {
        ConsoleHelper.printSubHeader("BILLS BY USER");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");
        try {
            List<Bill> bills = billService.getBillsByUser(userId);
            if (bills.isEmpty()) {
                ConsoleHelper.printInfo("No bills found for User ID: " + userId);
                return;
            }
            System.out.println();
            System.out.printf("  %-8s %-15s %-22s %-12s%n", "BillID", "Total", "Date", "Status");
            ConsoleHelper.printDivider();
            for (Bill b : bills) {
                System.out.printf("  %-8d %-15s %-22s %-12s%n",
                    b.getBillId(), b.getTotalAmount(), b.getBillDate(), b.getStatus());
            }
            ConsoleHelper.printDivider();
            ConsoleHelper.printInfo("Total bills: " + bills.size());
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to fetch bills: " + e.getMessage());
        }
    }

    private void viewBillDetails() {
        ConsoleHelper.printSubHeader("BILL DETAILS");
        int billId = ConsoleHelper.readInt(scanner, "Enter Bill ID: ");
        try {
            Bill bill = billService.getBillById(billId);
            System.out.println();
            System.out.println("  Bill ID      : " + bill.getBillId());
            System.out.println("  User ID      : " + bill.getUserId());
            System.out.println("  Total Amount : " + bill.getTotalAmount());
            System.out.println("  Date         : " + bill.getBillDate());
            System.out.println("  Status       : " + bill.getStatus());
            ConsoleHelper.printDivider();

            List<BillItem> items = billService.getBillItems(billId);
            if (items.isEmpty()) {
                ConsoleHelper.printInfo("No line items found for this bill.");
            } else {
                System.out.println();
                System.out.println("  Line Items:");
                System.out.printf("  %-10s %-12s %-10s %-15s%n", "ItemID", "ProductID", "Quantity", "Price");
                ConsoleHelper.printDivider();
                for (BillItem item : items) {
                    System.out.printf("  %-10d %-12d %-10d %-15s%n",
                        item.getBillItemId(), item.getProductId(), item.getQuantity(), item.getPriceAtPurchase());
                }
                ConsoleHelper.printDivider();
            }
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to fetch bill details: " + e.getMessage());
        }
    }
}

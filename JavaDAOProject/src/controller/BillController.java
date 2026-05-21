package controller;

import model.Bill;
import model.BillItem;
import service.BillService;

import java.math.BigDecimal;
import java.util.ArrayList;
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
            ConsoleHelper.printHeader("BILLS & ORDERS");
            System.out.println();
            ConsoleHelper.printMenuItem(1, "View Orders by User");
            ConsoleHelper.printMenuItem(2, "View Order Details");
            System.out.println();
            ConsoleHelper.printMenuBack();
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Choose an option");
            switch (choice) {
                case 1: viewBillsByUser(); break;
                case 2: viewBillDetails(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please select 0\u20132.");
            }
        }
    }

    private void viewBillsByUser() {
        ConsoleHelper.printSubHeader("ORDERS BY USER");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            List<Bill> bills = billService.getBillsByUser(userId);
            if (bills.isEmpty()) {
                ConsoleHelper.printWarning("No orders found for User #" + userId + ".");
            } else {
                String[] headers = {"Bill ID", "Total Amount", "Date", "Status"};
                List<String[]> rows = new ArrayList<>();
                for (Bill b : bills) {
                    String dateStr = b.getBillDate() != null
                        ? b.getBillDate().toLocalDate().toString()
                        : "N/A";

                    String statusStr;
                    if ("COMPLETED".equalsIgnoreCase(b.getStatus())) {
                        statusStr = ConsoleHelper.GREEN + b.getStatus() + ConsoleHelper.RESET;
                    } else if ("CANCELLED".equalsIgnoreCase(b.getStatus())) {
                        statusStr = ConsoleHelper.RED + b.getStatus() + ConsoleHelper.RESET;
                    } else {
                        statusStr = b.getStatus();
                    }

                    rows.add(new String[]{
                        "#" + b.getBillId(),
                        ConsoleHelper.formatCurrency(b.getTotalAmount()),
                        dateStr,
                        statusStr
                    });
                }
                ConsoleHelper.printTable(headers, rows);
                ConsoleHelper.printInfo(bills.size() + " order(s) found.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void viewBillDetails() {
        ConsoleHelper.printSubHeader("ORDER DETAILS");
        int billId = ConsoleHelper.readInt(scanner, "Enter Bill ID");

        try {
            Bill bill = billService.getBillById(billId);
            List<BillItem> items = billService.getBillItems(billId);

            // Bill summary
            System.out.println();
            ConsoleHelper.printKeyValue("Bill ID", "#" + bill.getBillId());
            ConsoleHelper.printKeyValue("User ID", String.valueOf(bill.getUserId()));
            ConsoleHelper.printKeyValue("Date", bill.getBillDate() != null
                ? bill.getBillDate().toString() : "N/A");

            String statusStr;
            if ("COMPLETED".equalsIgnoreCase(bill.getStatus())) {
                statusStr = ConsoleHelper.BOLD + ConsoleHelper.GREEN + bill.getStatus() + ConsoleHelper.RESET;
            } else {
                statusStr = ConsoleHelper.BOLD + ConsoleHelper.RED + bill.getStatus() + ConsoleHelper.RESET;
            }
            ConsoleHelper.printKeyValue("Status", statusStr);

            // Line items table
            if (items.isEmpty()) {
                ConsoleHelper.printWarning("No line items found for this bill.");
            } else {
                ConsoleHelper.printSubHeader("LINE ITEMS");

                String[] headers = {"Item ID", "Product ID", "Qty", "Unit Price", "Line Total"};
                List<String[]> rows = new ArrayList<>();
                List<String[]> receiptItems = new ArrayList<>();

                for (BillItem item : items) {
                    BigDecimal lineTotal = item.getPriceAtPurchase()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                    rows.add(new String[]{
                        String.valueOf(item.getBillItemId()),
                        "Product #" + item.getProductId(),
                        "x" + item.getQuantity(),
                        ConsoleHelper.formatCurrency(item.getPriceAtPurchase()),
                        ConsoleHelper.formatCurrency(lineTotal)
                    });

                    receiptItems.add(new String[]{
                        "Product #" + item.getProductId(),
                        "x" + item.getQuantity(),
                        ConsoleHelper.formatCurrency(item.getPriceAtPurchase()),
                        ConsoleHelper.formatCurrency(lineTotal)
                    });
                }

                ConsoleHelper.printTable(headers, rows);

                // Receipt-style total
                ConsoleHelper.printReceipt(
                    "INVOICE #" + billId,
                    receiptItems,
                    "GRAND TOTAL",
                    ConsoleHelper.formatCurrency(bill.getTotalAmount())
                );
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }
}

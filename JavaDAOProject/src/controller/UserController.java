package controller;

import model.User;
import model.Wallet;
import service.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserController {

    private final Scanner scanner;
    private final UserService userService;

    public UserController(Scanner scanner, UserService userService) {
        this.scanner = scanner;
        this.userService = userService;
    }

    public void showMenu() {
        boolean running = true;
        while (running) {
            ConsoleHelper.printHeader("USER MANAGEMENT");
            System.out.println();
            ConsoleHelper.printMenuItem(1, "View All Users");
            ConsoleHelper.printMenuItem(2, "Register New User");
            ConsoleHelper.printMenuItem(3, "View Wallet Balance");
            ConsoleHelper.printMenuItem(4, "Add Wallet Balance");
            System.out.println();
            ConsoleHelper.printMenuBack();
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Choose an option");
            switch (choice) {
                case 1: viewAllUsers(); break;
                case 2: registerUser(); break;
                case 3: viewWallet(); break;
                case 4: addWalletBalance(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please select 0\u20134.");
            }
        }
    }

    private void viewAllUsers() {
        ConsoleHelper.printSubHeader("ALL USERS");
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                ConsoleHelper.printWarning("No registered users found.");
            } else {
                String[] headers = {"ID", "Name", "Email"};
                List<String[]> rows = new ArrayList<>();
                for (User u : users) {
                    rows.add(new String[]{
                        String.valueOf(u.getUserId()),
                        u.getName(),
                        u.getEmail()
                    });
                }
                ConsoleHelper.printTable(headers, rows);
                ConsoleHelper.printInfo(users.size() + " user(s) registered.");
            }
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to load users: " + e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void registerUser() {
        ConsoleHelper.printSubHeader("REGISTER NEW USER");
        String name = ConsoleHelper.readString(scanner, "Full name");
        String email = ConsoleHelper.readString(scanner, "Email address");

        try {
            userService.registerUser(name, email);
            System.out.println();
            ConsoleHelper.printSuccess("User '" + name + "' registered successfully!");
            ConsoleHelper.printInfo("Wallet created with 0.00 balance.");
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void viewWallet() {
        ConsoleHelper.printSubHeader("WALLET BALANCE");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            Wallet wallet = userService.getWallet(userId);
            System.out.println();
            ConsoleHelper.printKeyValue("User ID", String.valueOf(wallet.getUserId()));
            ConsoleHelper.printKeyValue("Wallet ID", String.valueOf(wallet.getWalletId()));

            BigDecimal balance = wallet.getBalance();
            String balanceStr = ConsoleHelper.formatCurrency(balance);

            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                ConsoleHelper.printKeyValue("Balance",
                    ConsoleHelper.GREEN + balanceStr + ConsoleHelper.RESET);
            } else {
                ConsoleHelper.printKeyValue("Balance",
                    ConsoleHelper.YELLOW + balanceStr + "  (Empty)" + ConsoleHelper.RESET);
            }
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }

    private void addWalletBalance() {
        ConsoleHelper.printSubHeader("ADD WALLET BALANCE");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID");

        try {
            // Show current balance
            Wallet wallet = userService.getWallet(userId);
            ConsoleHelper.printInfo("Current balance: " + ConsoleHelper.formatCurrency(wallet.getBalance()));

            BigDecimal amount = ConsoleHelper.readBigDecimal(scanner, "Amount to add");
            userService.addWalletBalance(userId, amount);

            // Show new balance
            Wallet updated = userService.getWallet(userId);
            System.out.println();
            ConsoleHelper.printSuccess("Balance updated successfully!");
            ConsoleHelper.printKeyValue("Previous", ConsoleHelper.formatCurrency(wallet.getBalance()));
            ConsoleHelper.printKeyValue("Added", "+ " + ConsoleHelper.formatCurrency(amount));
            ConsoleHelper.printKeyValue("New Balance",
                ConsoleHelper.BOLD + ConsoleHelper.GREEN + ConsoleHelper.formatCurrency(updated.getBalance()) + ConsoleHelper.RESET);
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
        ConsoleHelper.pressEnterToContinue(scanner);
    }
}

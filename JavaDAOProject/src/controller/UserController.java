package controller;

import java.math.BigDecimal;
import model.User;
import model.Wallet;
import service.UserService;
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
            System.out.println("  1. View All Users");
            System.out.println("  2. Add New User");
            System.out.println("  3. View Wallet Balance");
            System.out.println("  4. Add Wallet Balance");
            System.out.println("  0. Back to Main Menu");
            ConsoleHelper.printDivider();

            int choice = ConsoleHelper.readInt(scanner, "Enter your choice: ");
            switch (choice) {
                case 1: viewAllUsers(); break;
                case 2: addUser(); break;
                case 3: viewWallet(); break;
                case 4: addWalletBalance(); break;
                case 0: running = false; break;
                default: ConsoleHelper.printError("Invalid choice. Please try again.");
            }
        }
    }

    private void viewAllUsers() {
        ConsoleHelper.printSubHeader("ALL USERS");
        try {
            List<User> users = userService.getAllUsers();
            if (users.isEmpty()) {
                ConsoleHelper.printInfo("No users found.");
                return;
            }
            System.out.println();
            System.out.printf("  %-6s %-20s %-30s%n", "ID", "Name", "Email");
            ConsoleHelper.printDivider();
            for (User u : users) {
                System.out.printf("  %-6d %-20s %-30s%n",
                    u.getUserId(), u.getName(), u.getEmail());
            }
            ConsoleHelper.printDivider();
            ConsoleHelper.printInfo("Total users: " + users.size());
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to fetch users: " + e.getMessage());
        }
    }

    private void addUser() {
        ConsoleHelper.printSubHeader("REGISTER NEW USER");
        String name = ConsoleHelper.readString(scanner, "Enter User Name: ");
        String email = ConsoleHelper.readString(scanner, "Enter Email: ");
        try {
            userService.registerUser(name, email);
            ConsoleHelper.printSuccess("User '" + name + "' registered successfully! (Wallet auto-created)");
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to register user: " + e.getMessage());
        }
    }

    private void viewWallet() {
        ConsoleHelper.printSubHeader("VIEW WALLET BALANCE");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");
        try {
            Wallet wallet = userService.getWallet(userId);
            System.out.println();
            System.out.println("  Wallet ID : " + wallet.getWalletId());
            System.out.println("  User ID   : " + wallet.getUserId());
            System.out.println("  Balance   : " + wallet.getBalance());
            ConsoleHelper.printDivider();
        } catch (Exception e) {
            ConsoleHelper.printError(e.getMessage());
        }
    }

    private void addWalletBalance() {
        ConsoleHelper.printSubHeader("ADD WALLET BALANCE");
        int userId = ConsoleHelper.readInt(scanner, "Enter User ID: ");
        BigDecimal amount = ConsoleHelper.readBigDecimal(scanner, "Enter Amount to Add: ");
        try {
            userService.addWalletBalance(userId, amount);
            ConsoleHelper.printSuccess("Added " + amount + " to wallet for User ID: " + userId);
            // Show updated balance
            Wallet wallet = userService.getWallet(userId);
            ConsoleHelper.printInfo("Updated Balance: " + wallet.getBalance());
        } catch (Exception e) {
            ConsoleHelper.printError("Failed to add balance: " + e.getMessage());
        }
    }
}

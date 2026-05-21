package controller;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleHelper {

    public static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("  [!] Invalid input. Please enter a valid integer.");
            scanner.next(); // consume bad input
            System.out.print(prompt);
        }
        int value = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return value;
    }

    public static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextBigDecimal()) {
            System.out.println("  [!] Invalid input. Please enter a valid number.");
            scanner.next();
            System.out.print(prompt);
        }
        BigDecimal value = scanner.nextBigDecimal();
        scanner.nextLine();
        return value;
    }

    public static String readString(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static void printHeader(String title) {
        System.out.println();
        System.out.println("===========================================");
        System.out.println("  " + title);
        System.out.println("===========================================");
    }

    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println("--- " + title + " ---");
    }

    public static void printSuccess(String message) {
        System.out.println("  [OK] " + message);
    }

    public static void printError(String message) {
        System.out.println("  [ERROR] " + message);
    }

    public static void printInfo(String message) {
        System.out.println("  [i] " + message);
    }

    public static void printDivider() {
        System.out.println("-------------------------------------------");
    }
}

package controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class ConsoleHelper {

    // ── ANSI Color Codes ──────────────────────────────────────────────
    public static final String RESET      = "\033[0m";
    public static final String BOLD       = "\033[1m";
    public static final String DIM        = "\033[2m";
    public static final String ITALIC     = "\033[3m";
    public static final String UNDERLINE  = "\033[4m";

    public static final String RED        = "\033[31m";
    public static final String GREEN      = "\033[32m";
    public static final String YELLOW     = "\033[33m";
    public static final String BLUE       = "\033[34m";
    public static final String MAGENTA    = "\033[35m";
    public static final String CYAN       = "\033[36m";
    public static final String WHITE      = "\033[37m";

    public static final String BRIGHT_RED     = "\033[91m";
    public static final String BRIGHT_GREEN   = "\033[92m";
    public static final String BRIGHT_YELLOW  = "\033[93m";
    public static final String BRIGHT_CYAN    = "\033[96m";
    public static final String BRIGHT_WHITE   = "\033[97m";

    public static final String BG_BLUE    = "\033[44m";
    public static final String BG_CYAN    = "\033[46m";

    private static final int TABLE_WIDTH = 60;
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    // ── Initialization ────────────────────────────────────────────────

    /**
     * Enable UTF-8 output on Windows terminals.
     */
    public static void enableWindowsAnsi() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                new ProcessBuilder("cmd", "/c", "chcp", "65001")
                    .inheritIO()
                    .start()
                    .waitFor();
            }
        } catch (Exception e) {
            // Silently ignore — ANSI may still work
        }
    }

    // ── Banner ────────────────────────────────────────────────────────

    public static void printBanner() {
        System.out.println();
        System.out.println(BOLD + CYAN +
            "      \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2557  \u2588\u2588\u2557  \u2588\u2588\u2588\u2588\u2588\u2557  \u2588\u2588\u2588\u2588\u2588\u2588\u2557 " + RESET);
        System.out.println(BOLD + CYAN +
            "      \u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255d \u2588\u2588\u2551  \u2588\u2588\u2551 \u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2557 \u2588\u2588\u2554\u2550\u2550\u2550\u2588\u2588\u2557" + RESET);
        System.out.println(BOLD + BRIGHT_CYAN +
            "      \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2557 \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2551 \u2588\u2588\u2551  \u2588\u2588\u2551 \u2588\u2588\u2588\u2588\u2588\u2588\u2554\u255d" + RESET);
        System.out.println(BOLD + CYAN +
            "           \u2588\u2588\u2551 \u2588\u2588\u2554\u2550\u2550\u2588\u2588\u2551 \u2588\u2588\u2551  \u2588\u2588\u2551 \u2588\u2588\u2554\u2550\u2550\u2550\u2550\u255d " + RESET);
        System.out.println(BOLD + CYAN +
            "      \u2588\u2588\u2588\u2588\u2588\u2588\u2588\u2551 \u2588\u2588\u2551  \u2588\u2588\u2551  \u2588\u2588\u2588\u2588\u2588\u2554\u255d \u2588\u2588\u2551     " + RESET);
        System.out.println(BOLD + CYAN +
            "      \u255a\u2550\u2550\u2550\u2550\u2550\u2550\u255d \u255a\u2550\u255d  \u255a\u2550\u255d  \u255a\u2550\u2550\u2550\u2550\u255d  \u255a\u2550\u255d     " + RESET);
        System.out.println();
        System.out.println(DIM + CYAN +  "      \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500" + RESET);
        System.out.println(BOLD + WHITE + "        Mini E-Commerce System  v2.0" + RESET);
        System.out.println(DIM +  "         Powered by Java + JDBC + DAO" + RESET);
        System.out.println(DIM + CYAN +  "      \u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500\u2500" + RESET);
        System.out.println();
    }

    // ── Headers ───────────────────────────────────────────────────────

    public static void printHeader(String title) {
        System.out.println();
        int width = Math.max(title.length() + 6, 45);
        String border = repeat("\u2550", width - 2);
        System.out.println(BOLD + CYAN + "  \u2554" + border + "\u2557" + RESET);
        System.out.println(BOLD + CYAN + "  \u2551  " + BRIGHT_WHITE + title + pad(width - title.length() - 5) + CYAN + "\u2551" + RESET);
        System.out.println(BOLD + CYAN + "  \u255a" + border + "\u255d" + RESET);
    }

    public static void printSubHeader(String title) {
        System.out.println();
        System.out.println(BOLD + YELLOW + "  \u25b8 " + title + RESET);
        System.out.println(DIM + "  " + repeat("\u2500", title.length() + 2) + RESET);
    }

    // ── Status Messages ───────────────────────────────────────────────

    public static void printSuccess(String message) {
        System.out.println(BOLD + GREEN + "  \u2714 " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(BOLD + RED + "  \u2718 " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "  \u26a0 " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(CYAN + "  \u2139 " + message + RESET);
    }

    // ── Menu Items ────────────────────────────────────────────────────

    public static void printMenuItem(int num, String label) {
        System.out.println("    " + BOLD + YELLOW + "[" + num + "]" + RESET + "  " + WHITE + label + RESET);
    }

    public static void printMenuBack() {
        System.out.println("    " + DIM + "[0]" + "  \u2190 Back" + RESET);
    }

    public static void printMenuExit() {
        System.out.println("    " + BOLD + RED + "[0]" + RESET + "  " + DIM + "Exit" + RESET);
    }

    // ── Dividers ──────────────────────────────────────────────────────

    public static void printDivider() {
        System.out.println(DIM + "  " + repeat("\u2500", 45) + RESET);
    }

    // ── Key-Value Display ─────────────────────────────────────────────

    public static void printKeyValue(String key, String value) {
        System.out.printf("    " + DIM + "%-18s" + RESET + " :  " + BRIGHT_WHITE + "%s" + RESET + "%n", key, value);
    }

    // ── Table Rendering ───────────────────────────────────────────────

    /**
     * Renders a professional Unicode box-drawing table.
     */
    public static void printTable(String[] headers, List<String[]> rows) {
        if (headers == null || headers.length == 0) return;

        int cols = headers.length;
        int[] widths = new int[cols];

        // Calculate column widths
        for (int i = 0; i < cols; i++) {
            widths[i] = headers[i].length();
        }
        if (rows != null) {
            for (String[] row : rows) {
                for (int i = 0; i < cols && i < row.length; i++) {
                    widths[i] = Math.max(widths[i], row[i] != null ? row[i].length() : 0);
                }
            }
        }
        // Add padding
        for (int i = 0; i < cols; i++) {
            widths[i] += 2;
        }

        // Top border
        System.out.print(DIM + "    \u250c");
        for (int i = 0; i < cols; i++) {
            System.out.print(repeat("\u2500", widths[i]));
            System.out.print(i < cols - 1 ? "\u252c" : "\u2510");
        }
        System.out.println(RESET);

        // Header row
        System.out.print("    " + DIM + "\u2502" + RESET);
        for (int i = 0; i < cols; i++) {
            System.out.print(BOLD + CYAN + " " + padRight(headers[i], widths[i] - 1) + RESET + DIM + "\u2502" + RESET);
        }
        System.out.println();

        // Header separator
        System.out.print(DIM + "    \u251c");
        for (int i = 0; i < cols; i++) {
            System.out.print(repeat("\u2500", widths[i]));
            System.out.print(i < cols - 1 ? "\u253c" : "\u2524");
        }
        System.out.println(RESET);

        // Data rows
        if (rows == null || rows.isEmpty()) {
            int totalWidth = 0;
            for (int w : widths) totalWidth += w;
            totalWidth += cols - 1; // separators
            String msg = "No records found";
            int leftPad = (totalWidth - msg.length()) / 2;
            System.out.print("    " + DIM + "\u2502" + RESET);
            System.out.print(DIM + ITALIC + pad(leftPad) + msg + pad(totalWidth - leftPad - msg.length()) + RESET);
            System.out.println(DIM + "\u2502" + RESET);
        } else {
            for (int r = 0; r < rows.size(); r++) {
                String[] row = rows.get(r);
                String rowColor = (r % 2 == 0) ? WHITE : BRIGHT_WHITE;
                System.out.print("    " + DIM + "\u2502" + RESET);
                for (int i = 0; i < cols; i++) {
                    String val = (i < row.length && row[i] != null) ? row[i] : "";
                    System.out.print(rowColor + " " + padRight(val, widths[i] - 1) + RESET + DIM + "\u2502" + RESET);
                }
                System.out.println();
            }
        }

        // Bottom border
        System.out.print(DIM + "    \u2514");
        for (int i = 0; i < cols; i++) {
            System.out.print(repeat("\u2500", widths[i]));
            System.out.print(i < cols - 1 ? "\u2534" : "\u2518");
        }
        System.out.println(RESET);
    }

    // ── Receipt Rendering ─────────────────────────────────────────────

    public static void printReceipt(String title, List<String[]> items, String totalLabel, String totalValue) {
        int width = 50;
        System.out.println();
        System.out.println(DIM + "    " + repeat("\u2504", width) + RESET);
        System.out.println(BOLD + WHITE + "    " + centerText(title, width) + RESET);
        System.out.println(DIM + "    " + repeat("\u2504", width) + RESET);

        if (items != null) {
            for (String[] item : items) {
                StringBuilder line = new StringBuilder("    ");
                for (int i = 0; i < item.length; i++) {
                    if (i == item.length - 1) {
                        // Right-align last column
                        int usedWidth = line.length() - 4;
                        int remaining = width - usedWidth - item[i].length();
                        line.append(pad(Math.max(1, remaining)));
                    } else {
                        line.append("  ");
                    }
                    line.append(item[i]);
                }
                System.out.println(WHITE + line.toString() + RESET);
            }
        }

        System.out.println(DIM + "    " + repeat("\u2504", width) + RESET);
        String totalLine = "    " + BOLD + totalLabel;
        int remaining = width - totalLabel.length() - totalValue.length();
        totalLine += pad(Math.max(1, remaining)) + GREEN + totalValue + RESET;
        System.out.println(totalLine);
        System.out.println(DIM + "    " + repeat("\u2504", width) + RESET);
        System.out.println();
    }

    // ── Input Methods ─────────────────────────────────────────────────

    public static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print("\n  " + BOLD + YELLOW + "\u276f " + RESET + DIM + prompt + ": " + RESET);
            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return value;
            } else {
                scanner.next(); // consume bad input
                printError("Invalid input. Please enter a whole number.");
            }
        }
    }

    public static BigDecimal readBigDecimal(Scanner scanner, String prompt) {
        while (true) {
            System.out.print("\n  " + BOLD + YELLOW + "\u276f " + RESET + DIM + prompt + ": " + RESET);
            if (scanner.hasNextBigDecimal()) {
                BigDecimal value = scanner.nextBigDecimal();
                scanner.nextLine();
                return value;
            } else {
                scanner.next();
                printError("Invalid input. Please enter a valid number.");
            }
        }
    }

    public static String readString(Scanner scanner, String prompt) {
        System.out.print("\n  " + BOLD + YELLOW + "\u276f " + RESET + DIM + prompt + ": " + RESET);
        return scanner.nextLine().trim();
    }

    public static boolean readConfirmation(Scanner scanner, String prompt) {
        System.out.print("\n  " + BOLD + YELLOW + "\u276f " + RESET + YELLOW + prompt + " (y/n): " + RESET);
        String input = scanner.nextLine().trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    // ── Formatting Helpers ────────────────────────────────────────────

    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) return "\u20b90.00";
        return CURRENCY_FORMAT.format(amount);
    }

    public static void pressEnterToContinue(Scanner scanner) {
        System.out.println();
        System.out.print(DIM + "  Press Enter to continue..." + RESET);
        scanner.nextLine();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // ── String Utilities ──────────────────────────────────────────────

    private static String repeat(String s, int count) {
        if (count <= 0) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) sb.append(s);
        return sb.toString();
    }

    private static String pad(int count) {
        return repeat(" ", count);
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s;
        return s + pad(width - s.length());
    }

    private static String centerText(String text, int width) {
        if (text.length() >= width) return text;
        int leftPad = (width - text.length()) / 2;
        return pad(leftPad) + text;
    }
}

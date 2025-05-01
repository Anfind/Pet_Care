package com.petcare.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Utility class for console input/output operations.
 */
public class ConsoleUtil {
    private static final Scanner scanner = new Scanner(System.in);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    // ANSI color codes for terminal colors
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";
    
    // Background colors
    public static final String BG_BLACK = "\u001B[40m";
    public static final String BG_RED = "\u001B[41m";
    public static final String BG_GREEN = "\u001B[42m";
    public static final String BG_YELLOW = "\u001B[43m";
    public static final String BG_BLUE = "\u001B[44m";
    public static final String BG_PURPLE = "\u001B[45m";
    public static final String BG_CYAN = "\u001B[46m";
    public static final String BG_WHITE = "\u001B[47m";
    
    // Text styles
    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";
    
    /**
     * Reads a non-empty string from the console.
     * 
     * @param prompt The prompt to display
     * @return The input string
     */
    public static String readString(String prompt) {
        String input;
        do {
            System.out.print(BOLD + CYAN + prompt + ": " + RESET);
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println(YELLOW + "Input cannot be empty. Please try again." + RESET);
            }
        } while (input.isEmpty());
        return input;
    }
    
    /**
     * Reads an optional string from the console (can be empty).
     * 
     * @param prompt The prompt to display
     * @return The input string, which may be empty
     */
    public static String readOptionalString(String prompt) {
        System.out.print(BOLD + CYAN + prompt + " (optional): " + RESET);
        return scanner.nextLine().trim();
    }
    
    /**
     * Reads a password from the console (doesn't display characters).
     * Note: In a real console app, we would use System.console().readPassword()
     * but for simplicity we're using the Scanner here.
     * 
     * @param prompt The prompt to display
     * @return The password string
     */
    public static String readPassword(String prompt) {
        return readString(prompt + " (input is visible)");
    }
    
    /**
     * Reads an integer from the console.
     * 
     * @param prompt The prompt to display
     * @return The input integer
     */
    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(BOLD + CYAN + prompt + ": " + RESET);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(YELLOW + "Please enter a valid integer." + RESET);
            }
        }
    }
    
    /**
     * Reads an integer within a specified range from the console.
     * 
     * @param prompt The prompt to display
     * @param min The minimum allowed value
     * @param max The maximum allowed value
     * @return The input integer within the specified range
     */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt + " (" + min + "-" + max + ")");
            if (value >= min && value <= max) {
                return value;
            } else {
                System.out.println(YELLOW + "Please enter a value between " + min + " and " + max + "." + RESET);
            }
        }
    }
    
    /**
     * Reads a long integer from the console.
     * 
     * @param prompt The prompt to display
     * @return The input long integer
     */
    public static Long readLong(String prompt) {
        while (true) {
            try {
                System.out.print(BOLD + CYAN + prompt + ": " + RESET);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return 0L; // Optional: return 0 for empty input
                }
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println(YELLOW + "Please enter a valid number." + RESET);
            }
        }
    }
    
    /**
     * Reads a decimal number from the console.
     * 
     * @param prompt The prompt to display
     * @return The input decimal as a BigDecimal
     */
    public static BigDecimal readBigDecimal(String prompt) {
        while (true) {
            try {
                System.out.print(BOLD + CYAN + prompt + ": " + RESET);
                String input = scanner.nextLine().trim();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println(YELLOW + "Please enter a valid decimal number." + RESET);
            }
        }
    }
    
    /**
     * Reads a date and time from the console.
     * 
     * @param prompt The prompt to display
     * @return The input date and time as a Timestamp
     */
    public static Timestamp readTimestamp(String prompt) {
        while (true) {
            try {
                System.out.print(BOLD + CYAN + prompt + " (format: yyyy-MM-dd HH:mm): " + RESET);
                String input = scanner.nextLine().trim();
                Date date = dateFormat.parse(input);
                return new Timestamp(date.getTime());
            } catch (ParseException e) {
                System.out.println(YELLOW + "Please enter a valid date and time in the format: yyyy-MM-dd HH:mm" + RESET);
            }
        }
    }
    
    /**
     * Reads a Yes/No response from the console.
     * 
     * @param prompt The prompt to display
     * @return true if the answer is Yes, false if No
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(BOLD + CYAN + prompt + " (Y/N): " + RESET);
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("Y")) {
                return true;
            } else if (input.equals("N")) {
                return false;
            } else {
                System.out.println(YELLOW + "Please enter Y for Yes or N for No." + RESET);
            }
        }
    }
    
    /**
     * Displays a menu and reads the selected option.
     * 
     * @param title The menu title
     * @param options The menu options
     * @return The selected option index (0-based)
     */
    public static int displayMenu(String title, String[] options) {
        int width = getMaxWidth(title, options) + 10;
        
        printBoxTop(width);
        printCenteredText(title, width);
        printBoxMiddle(width);
        
        for (int i = 0; i < options.length; i++) {
            System.out.println(BOLD + "│  " + CYAN + (i + 1) + ". " + 
                    GREEN + options[i] + RESET + " ".repeat(width - options[i].length() - 7) + BOLD + "│" + RESET);
        }
        
        System.out.println(BOLD + "│  " + RED + "0. Back/Exit" + RESET + " ".repeat(width - 14) + BOLD + "│" + RESET);
        printBoxBottom(width);
        
        return readInt("Enter your choice", 0, options.length);
    }
    
    /**
     * Prints a message and waits for the user to press Enter.
     * 
     * @param message The message to display
     */
    public static void pressEnterToContinue(String message) {
        if (!message.isEmpty()) {
            System.out.println(BOLD + PURPLE + message + RESET);
        }
        System.out.print(YELLOW + "Press Enter to continue..." + RESET);
        scanner.nextLine();
    }
    
    /**
     * Displays a title with decorative border.
     * 
     * @param title The title to display
     */
    public static void displayTitle(String title) {
        int width = title.length() + 8;
        
        System.out.println();
        System.out.println(BOLD + BG_BLUE + WHITE + " ".repeat(width) + RESET);
        System.out.println(BOLD + BG_BLUE + WHITE + "    " + title + "    " + RESET);
        System.out.println(BOLD + BG_BLUE + WHITE + " ".repeat(width) + RESET);
        System.out.println();
    }

    /**
     * Prompts the user to confirm if they want to continue iterating or performing an action
     * @param scanner Scanner object to read user input
     * @param promptMessage Custom message to display (defaults to "Continue to iterate?")
     * @return true if user wants to continue, false otherwise
     */
    public static boolean continueIteration(Scanner scanner, String promptMessage) {
        String message = promptMessage != null ? promptMessage : "Continue to iterate?";
        System.out.print(BOLD + CYAN + message + " (y/n): " + RESET);
        String response = scanner.nextLine().trim().toLowerCase();
        return response.equals("y") || response.equals("yes");
    }

    /**
     * Prompts the user to confirm if they want to continue iterating with default message
     * @param scanner Scanner object to read user input
     * @return true if user wants to continue, false otherwise
     */
    public static boolean continueIteration(Scanner scanner) {
        return continueIteration(scanner, "Continue to iterate?");
    }
    
    /**
     * Draws a nice table header with column names
     * 
     * @param headers The column headers
     * @param widths The widths for each column
     */
    public static void drawTableHeader(String[] headers, int[] widths) {
        // Top border
        System.out.print(BOLD + "┌");
        for (int i = 0; i < widths.length; i++) {
            System.out.print("─".repeat(widths[i] + 2));
            System.out.print(i < widths.length - 1 ? "┬" : "┐");
        }
        System.out.println(RESET);
        
        // Headers
        System.out.print(BOLD + "│");
        for (int i = 0; i < headers.length; i++) {
            System.out.print(CYAN + " " + headers[i] + " ".repeat(widths[i] - headers[i].length() + 1) + RESET + BOLD + "│");
        }
        System.out.println(RESET);
        
        // Middle border
        System.out.print(BOLD + "├");
        for (int i = 0; i < widths.length; i++) {
            System.out.print("─".repeat(widths[i] + 2));
            System.out.print(i < widths.length - 1 ? "┼" : "┤");
        }
        System.out.println(RESET);
    }
    
    /**
     * Draws a table row with data
     * 
     * @param data The row data
     * @param widths The widths for each column
     */
    public static void drawTableRow(String[] data, int[] widths) {
        System.out.print(BOLD + "│" + RESET);
        for (int i = 0; i < data.length; i++) {
            String cellData = data[i];
            if (cellData.length() > widths[i]) {
                cellData = cellData.substring(0, widths[i] - 3) + "...";
            }
            System.out.print(" " + cellData + " ".repeat(widths[i] - cellData.length() + 1));
            System.out.print(BOLD + "│" + RESET);
        }
        System.out.println();
    }
    
    /**
     * Draws the table footer (bottom border)
     * 
     * @param widths The widths for each column
     */
    public static void drawTableFooter(int[] widths) {
        System.out.print(BOLD + "└");
        for (int i = 0; i < widths.length; i++) {
            System.out.print("─".repeat(widths[i] + 2));
            System.out.print(i < widths.length - 1 ? "┴" : "┘");
        }
        System.out.println(RESET);
    }
    
    /**
     * Displays a status with appropriate color
     * 
     * @param status The status text
     * @return Colored status text
     */
    public static String colorizeStatus(String status) {
        status = status.toUpperCase();
        
        if (status.equals("COMPLETED") || status.equals("DELIVERED") || status.equals("APPROVED")) {
            return GREEN + status + RESET;
        } else if (status.equals("PENDING") || status.equals("SCHEDULED")) {
            return YELLOW + status + RESET;
        } else if (status.equals("CANCELLED") || status.equals("FAILED")) {
            return RED + status + RESET;
        } else if (status.equals("PROCESSING") || status.equals("SHIPPED")) {
            return CYAN + status + RESET;
        } else {
            return PURPLE + status + RESET;
        }
    }
    
    /**
     * Display star rating (e.g. ★★★☆☆)
     * 
     * @param rating The numeric rating (1-5)
     * @return Star representation of the rating
     */
    public static String getStarRating(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append(YELLOW).append("★");
            } else {
                stars.append(WHITE).append("☆");
            }
        }
        stars.append(RESET);
        return stars.toString();
    }
    
    // Helper methods for box drawing
    
    private static int getMaxWidth(String title, String[] options) {
        int max = title.length();
        for (String option : options) {
            if (option.length() > max) {
                max = option.length();
            }
        }
        return max;
    }
    
    public static void printBoxTop(int width) {
        System.out.print(BOLD + "┌");
        System.out.print("─".repeat(width));
        System.out.println("┐" + RESET);
    }
    
    public static void printBoxMiddle(int width) {
        System.out.print(BOLD + "├");
        System.out.print("─".repeat(width));
        System.out.println("┤" + RESET);
    }
    
    public static void printBoxBottom(int width) {
        System.out.print(BOLD + "└");
        System.out.print("─".repeat(width));
        System.out.println("┘" + RESET);
    }
    
    public static void printCenteredText(String text, int width) {
        int padding = (width - text.length()) / 2;
        String paddedText = " ".repeat(padding) + text + " ".repeat(width - text.length() - padding);
        System.out.println(BOLD + "│" + BG_BLUE + WHITE + paddedText + RESET + BOLD + "│" + RESET);
    }
    
    /**
     * Clears the console screen
     * Note: This may not work in all environments
     */
    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

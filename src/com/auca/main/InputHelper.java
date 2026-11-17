package com.auca.main;

import com.auca.exceptions.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class InputHelper {
    // Section 6 & 12: One single shared Scanner reading from System.in
    private static final Scanner SCANNER = new Scanner(System.in);

    // Control signals for multi-step form navigation
    public static class FormBackException extends Exception {
        public FormBackException() { super("BACK"); }
    }

    public static class FormCancelException extends Exception {
        public FormCancelException() { super("CANCEL"); }
    }

    public static String readRawLine(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    public static String readFormString(String prompt, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();

            if (input.equalsIgnoreCase("CANCEL")) {
                throw new FormCancelException();
            }
            if (allowBack && input.equalsIgnoreCase("BACK")) {
                throw new FormBackException();
            }
            if (input.isEmpty()) {
                System.out.println("! Input cannot be blank. Please enter a value (or type CANCEL to exit).");
                continue;
            }
            return input;
        }
    }

    public static int readFormInt(String prompt, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String input = readFormString(prompt, allowBack);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("! Invalid whole number: '" + input + "'. Please enter digits only.");
            }
        }
    }

    public static double readFormDouble(String prompt, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String input = readFormString(prompt, allowBack);
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("! Invalid decimal number: '" + input + "'. Please enter a numeric score.");
            }
        }
    }

    public static BigDecimal readFormBigDecimal(String prompt, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String input = readFormString(prompt, allowBack);
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("! Invalid monetary amount: '" + input + "'. Please enter a valid positive number (e.g., 250000.00).");
            }
        }
    }

    public static LocalDate readFormDate(String prompt, boolean allowBack) throws FormBackException, FormCancelException {
        while (true) {
            String input = readFormString(prompt, allowBack);
            try {
                return LocalDate.parse(input);
            } catch (DateTimeParseException e) {
                System.out.println("! Invalid date: use YYYY-MM-DD (example: 2004-05-16).");
            }
        }
    }

    public static boolean readFormBooleanChoice(String prompt, String trueLabel, String falseLabel, boolean allowBack)
            throws FormBackException, FormCancelException {
        while (true) {
            String input = readFormString(prompt, allowBack);
            if (input.equals("1") || input.equalsIgnoreCase(trueLabel)) {
                return true;
            }
            if (input.equals("2") || input.equalsIgnoreCase(falseLabel)) {
                return false;
            }
            System.out.println("! Please enter 1 (" + trueLabel + ") or 2 (" + falseLabel + ").");
        }
    }

    public static int readMenuChoice(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                int choice = Integer.parseInt(input);
                if (choice < min || choice > max) {
                    System.out.println("! Option out of range. Please choose between " + min + " and " + max + ".");
                    continue;
                }
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("! Invalid selection. Please enter a number between " + min + " and " + max + ".");
            }
        }
    }

    public static boolean confirmAction(String prompt) {
        while (true) {
            System.out.print(prompt + " [1 YES / 2 NO]: ");
            String input = SCANNER.nextLine().trim();
            if (input.equals("1") || input.equalsIgnoreCase("YES") || input.equalsIgnoreCase("Y")) {
                return true;
            }
            if (input.equals("2") || input.equalsIgnoreCase("NO") || input.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("! Please enter 1 for YES or 2 for NO.");
        }
    }

    // Inline validator helper so single fields fail fast during entry before reaching Review
    public static void checkFieldRule(RunnableValidator rule) throws ValidationException {
        rule.validate();
    }

    @FunctionalInterface
    public interface RunnableValidator {
        void validate() throws ValidationException;
    }
}

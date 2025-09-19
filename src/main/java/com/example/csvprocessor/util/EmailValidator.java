package com.example.csvprocessor.util;

import java.util.regex.Pattern;

/**
 * Utility class for validating email addresses using a regular expression.
 * <p>
 * Supports case-insensitive matching and checks for standard email format.
 */
public class EmailValidator {

    /**
     * Precompiled regex pattern for validating email addresses.
     * <p>
     * Matches formats like {@code user@example.com}, allowing uppercase letters,
     * digits, and common symbols.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Validates whether the given input string is a properly formatted email address.
     *
     * @param input the string to validate
     * @return {@code true} if the input matches the email pattern; {@code false} otherwise
     */
    public static boolean isValidEmail(String input) {
        return input != null && EMAIL_PATTERN.matcher(input.trim()).matches();
    }
}
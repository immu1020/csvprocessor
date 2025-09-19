package com.example.csvprocessor.util;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );

    public static boolean isValidEmail(String input) {
        return input != null && EMAIL_PATTERN.matcher(input.trim()).matches();
    }
}
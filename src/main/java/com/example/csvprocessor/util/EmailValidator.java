package com.example.csvprocessor.util;

import java.util.regex.Pattern;

public class EmailValidator {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    public static boolean isValidEmail(String input) {
        return EMAIL_REGEX.matcher(input.trim()).matches();
    }
}

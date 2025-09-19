package com.example.csvprocessor.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailValidatorTest {

    @Test
    void validEmailsShouldPass() {
        assertTrue(EmailValidator.isValidEmail("user@example.com"));
        assertTrue(EmailValidator.isValidEmail("user.name+tag@sub.domain.co.in"));
        assertTrue(EmailValidator.isValidEmail("USER@EXAMPLE.COM"));
    }

    @Test
    void invalidEmailsShouldFail() {
        assertFalse(EmailValidator.isValidEmail("user@.com"));
        assertFalse(EmailValidator.isValidEmail("@example.com"));
        assertFalse(EmailValidator.isValidEmail("userexample.com"));
        assertFalse(EmailValidator.isValidEmail("user@com"));
        assertFalse(EmailValidator.isValidEmail(""));
        assertFalse(EmailValidator.isValidEmail(null));
    }
}

package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ValidationExceptionTest {

    @Test
    void shouldCreateValidationExceptionWithMessage() {
        ValidationException exception = new ValidationException("Validation failed");
        assertEquals("Validation failed", exception.getMessage());
    }
}

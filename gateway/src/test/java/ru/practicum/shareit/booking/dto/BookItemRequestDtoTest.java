package ru.practicum.shareit.booking.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utility.Create;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookItemRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidBookingRequest_shouldPassValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingRequest.setEnd(LocalDateTime.now().plusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertTrue(violations.isEmpty(), "Valid booking request should not have validation errors");
    }

    @Test
    void whenInvalidBookingRequest_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().minusMinutes(5));
        bookingRequest.setEnd(LocalDateTime.now().minusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertFalse(violations.isEmpty(), "Invalid booking request should have validation errors");
    }

    @Test
    void whenStartAfterEnd_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusHours(1));
        bookingRequest.setEnd(LocalDateTime.now().minusHours(5));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertFalse(violations.isEmpty(), "Start after end booking request should have validation errors");
    }

    @Test
    void whenStartBeforeNow_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().minusMinutes(5));
        bookingRequest.setEnd(LocalDateTime.now().plusMinutes(10));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertFalse(violations.isEmpty(), "Start before now booking request should have validation errors");
    }

    @Test
    void whenEndBeforeNow_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(1L);
        bookingRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingRequest.setEnd(LocalDateTime.now().minusMinutes(5));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertFalse(violations.isEmpty(), "End before now booking request should have validation errors");
    }

    @Test
    void whenItemIdIsNull_shouldFailValidation() {
        BookItemRequestDto bookingRequest = new BookItemRequestDto();
        bookingRequest.setItemId(null);
        bookingRequest.setStart(LocalDateTime.now().plusMinutes(10));
        bookingRequest.setEnd(LocalDateTime.now().plusHours(1));

        Set<ConstraintViolation<BookItemRequestDto>> violations = validator.validate(bookingRequest, Create.class);
        assertFalse(violations.isEmpty(), "Item ID is null booking request should have validation errors");
    }

}
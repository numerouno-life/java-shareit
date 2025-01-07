package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utility.Create;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ItemDtoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidItemRequest_shouldPassValidation() {
        ItemDtoRequest itemRequest = new ItemDtoRequest();
        itemRequest.setId(1L);
        itemRequest.setName("Test Item");
        itemRequest.setDescription("Description");
        itemRequest.setAvailable(true);
        itemRequest.setRequestId(1L);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(itemRequest);
        assertTrue(violations.isEmpty(), "Valid item request should not have validation errors");
    }

    @Test
    void whenInvalidItemRequest_shouldFailValidation() {
        ItemDtoRequest itemRequest = new ItemDtoRequest();
        itemRequest.setId(1L);
        itemRequest.setName("");

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(itemRequest);
        assertFalse(violations.isEmpty(), "Item with blank name should have a validation error");
    }

    @Test
    void whenDescriptionIsBlank_shouldFailValidation() {
        ItemDtoRequest itemRequest = new ItemDtoRequest();
        itemRequest.setId(1L);
        itemRequest.setName("Test Item");
        itemRequest.setDescription("");
        itemRequest.setAvailable(true);

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(itemRequest, Create.class);
        assertEquals(1, violations.size(),
                "Item with blank description should have a validation error");
        assertEquals("не должно быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void whenAvailableIsMissing_shouldFailValidation() {
        ItemDtoRequest itemRequest = new ItemDtoRequest();
        itemRequest.setId(1L);
        itemRequest.setName("Test Item");
        itemRequest.setDescription("Description");

        Set<ConstraintViolation<ItemDtoRequest>> violations = validator.validate(itemRequest, Create.class);
        assertEquals(1, violations.size(),
                "Item with missing availability should have a validation error");
        assertEquals("не должно равняться null", violations.iterator().next().getMessage());
    }


}
package ru.practicum.shareit.item.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.utility.Create;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommentDtoRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidCommentRequest_shouldPassValidation() {
        CommentDtoRequest commentRequest = new CommentDtoRequest();
        commentRequest.setId(1L);
        commentRequest.setText("Test comment");

        Set<ConstraintViolation<CommentDtoRequest>> violations = validator.validate(commentRequest);
        assertTrue(violations.isEmpty(), "Valid comment request should not have validation errors");
    }

    @Test
    void whenInvalidCommentRequest_shouldFailValidation() {
        CommentDtoRequest commentRequest = new CommentDtoRequest();
        commentRequest.setId(1L);
        commentRequest.setText("");

        Set<ConstraintViolation<CommentDtoRequest>> violations = validator.validate(commentRequest, Create.class);
        assertFalse(violations.isEmpty(), "Comment with blank text should have a validation error");
    }
}
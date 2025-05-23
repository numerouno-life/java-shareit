package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MissingRequestHeaderException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFound_shouldReturnNotFoundErrorResponse() {
        NotFoundException e = new NotFoundException("Not found");
        ErrorResponse response = errorHandler.handleNotFound(e);
        assertEquals("Not found", response.getError());
    }

    @Test
    void handleValidation_shouldReturnBadRequestErrorResponse() {
        ValidationException e = new ValidationException("Invalid input");
        ErrorResponse response = errorHandler.handleValidation(e);
        assertEquals("Invalid input", response.getError());
    }

    @Test
    void handleMissingRequestHeaderException_shouldReturnErrorMap() {
        MissingRequestHeaderException exception = new MissingRequestHeaderException("X-Header", null);
        Map<String, String> response = errorHandler.handleMissingRequestHeaderException(exception);

        assertEquals("Отсутствует обязательный заголовок: X-Header", response.get("error"));
    }

    @Test
    void handle_shouldReturnInternalServerErrorResponse() {
        Exception e = new Exception("Unexpected error");
        ErrorResponse response = errorHandler.handle(e);
        assertEquals("Произошла ошибка на стороне сервера", response.getError());
    }

    @Test
    void handleConflict_shouldReturnConflictErrorResponse() {
        ConflictException e = new ConflictException("Conflict occurred");
        ErrorResponse response = errorHandler.handleConflict(e);
        assertEquals("Conflict occurred", response.getError());
    }

    @Test
    void handleAuthorizationException_shouldReturnForbiddenErrorResponse() {
        AuthorizationException e = new AuthorizationException("Access denied");
        ErrorResponse response = errorHandler.handleAuthorizationException(e);
        assertEquals("Access denied", response.getError());
    }

    @Test
    void internalServer_shouldReturnInternalServerErrorResponse() {
        InternalServerException e = new InternalServerException("Server error");
        ErrorResponse response = errorHandler.internalServer(e);
        assertEquals("Server error", response.getError());
    }


}

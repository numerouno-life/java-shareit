package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleInternalServerExceptions_shouldReturnInternalServerErrorResponse() {
        Exception e = new Exception("Internal server error");
        ErrorResponse response = errorHandler.handleInternalServerExceptions(e);

        assertEquals("Произошла ошибка на стороне сервера", response.getError());
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequestResponse() {
        MethodArgumentNotValidException e = mock(MethodArgumentNotValidException.class);
        ErrorResponse response = errorHandler.handleValidationExceptions(e);

        assertEquals("MethodArgumentNotValidException", response.getError());
    }
}

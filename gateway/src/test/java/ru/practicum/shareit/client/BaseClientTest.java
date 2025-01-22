package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BaseClientTest {

    private BaseClient baseClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        baseClient = new BaseClient(restTemplate);
    }

    @Test
    void shouldSendGetRequestWithHeaders() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldSendPutRequestWithHeaders() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.put(url, userId, new Object());

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.PUT), any(), eq(Object.class));
    }

    @Test
    void shouldSendPostRequestWithHeaders() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.POST),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.post(url, userId, new Object());

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.POST), any(), eq(Object.class));
    }

    @Test
    void shouldSendDeleteRequestWithHeaders() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.delete(url, userId);

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.DELETE), any(), eq(Object.class));
    }

    @Test
    void shouldHandleHttpStatusCodeException() {
        String url = "/test-path";
        Long userId = 1L;
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(400, result.getStatusCodeValue());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldSendGetRequestWithParameters() {
        String url = "/test-path";
        Long userId = 1L;
        Map<String, Object> parameters = Map.of("key", "value");
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(parameters)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId, parameters);

        assertEquals(response, result);
        verify(restTemplate, times(1))
                .exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class), eq(parameters));
    }

    @Test
    void shouldCreateHeadersWithoutUserId() {
        String url = "/test-path";
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url);

        assertEquals(response, result);
        verify(restTemplate, times(1))
                .exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldHandleResponseWithoutBody() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEquals(null, result.getBody());
    }

    @Test
    void shouldHandleResponseWithBody() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).body("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Success", result.getBody());
    }

    @Test
    void shouldSendPatchRequestWithHeaders() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.patch(url, userId, new Object());

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.PATCH), any(), eq(Object.class));
    }

    @Test
    void shouldSendDeleteRequestWithoutUserId() {
        String url = "/test-path";
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.DELETE),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.delete(url);

        assertEquals(response, result);
        verify(restTemplate, times(1))
                .exchange(eq(url), eq(HttpMethod.DELETE), any(), eq(Object.class));
    }

    @Test
    void shouldHandleHttpStatusCodeExceptionInPatch() {
        String url = "/test-path";
        Long userId = 1L;
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.patch(url, userId, new Object());

        assertEquals(400, result.getStatusCodeValue());
        verify(restTemplate, times(1))
                .exchange(eq(url), eq(HttpMethod.PATCH), any(), eq(Object.class));
    }

    @Test
    void shouldHandleNullUserIdInHeaders() {
        String url = "/test-path";
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, 1L);

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url),
                eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldHandleNullParametersInGet() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId, null);

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldHandleComplexParametersInGet() {
        String url = "/test-path";
        Long userId = 1L;
        Map<String, Object> parameters = Map.of("key", Map.of("nestedKey", "nestedValue"));
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                eq(parameters)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId, parameters);

        assertEquals(response, result);
        verify(restTemplate, times(1))
                .exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class), eq(parameters));
    }

    @Test
    void shouldHandleRestClientException() {
        String url = "/test-path";
        Long userId = 1L;
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> baseClient.get(url, userId));

        assertEquals("Unexpected error", exception.getMessage());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldHandleHttpServerErrorException() {
        String url = "/test-path";
        Long userId = 1L;
        String errorMessage = "Server error";
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(errorMessage, exception.getStatusText());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldHandleResponseWithNullBody() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEquals(null, result.getBody());
    }

    @Test
    void shouldHandleHttpStatusNotFound() {
        String url = "/test-path";
        Long userId = 1L;
        HttpStatusCodeException exception = mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(404, result.getStatusCodeValue());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldHandleHttpServerErrorExceptionWithCustomMessage() {
        String url = "/test-path";
        Long userId = 1L;
        String errorMessage = "Server error occurred";
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenThrow(exception);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals(errorMessage, exception.getStatusText());
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldHandleSuccessfulResponseWithBody() {
        String url = "/test-path";
        Long userId = 1L;
        String responseBody = "Response body content";
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.OK).body(responseBody);
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(responseBody, result.getBody());
    }

    @Test
    void shouldHandleEmptyResponseWithNoBody() {
        String url = "/test-path";
        Long userId = 1L;
        ResponseEntity<Object> response = ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.get(url, userId);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
    }

    @Test
    void shouldSendPutRequestWithoutUserId() {
        String url = "/test-path";
        ResponseEntity<Object> response = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(url),
                eq(HttpMethod.PUT),
                any(),
                eq(Object.class)))
                .thenReturn(response);

        ResponseEntity<Object> result = baseClient.put(url, 0L, new Object());

        assertEquals(response, result);
        verify(restTemplate, times(1)).exchange(eq(url), eq(HttpMethod.PUT), any(), eq(Object.class));
    }
}

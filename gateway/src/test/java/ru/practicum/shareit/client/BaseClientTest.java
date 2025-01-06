package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}

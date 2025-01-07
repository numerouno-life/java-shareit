package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ItemRequestClientTest {
    private ItemRequestClient itemRequestClient;
    private RestTemplate restTemplate;

    @Value("${shareit-server.url}")
    private final String serverUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        RestTemplateBuilder restTemplateBuilder = Mockito.mock(RestTemplateBuilder.class);

        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        itemRequestClient = new ItemRequestClient("http://localhost:8080", restTemplateBuilder);
    }

    @Test
    void shouldGetAllRequests() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 10;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getAllRequests(userId, from, size);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class));
    }

    @Test
    void shouldGetRequestByIdWithItems() {
        Long userId = 1L;
        Long requestId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getRequestByIdWithItems(userId, requestId);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(), eq(Object.class));
    }

}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;

import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingClientTest {
    private BookingClient bookingClient;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        restTemplate = Mockito.mock(RestTemplate.class);
        RestTemplateBuilder restTemplateBuilder = Mockito.mock(RestTemplateBuilder.class);

        when(restTemplateBuilder.uriTemplateHandler(any())).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.requestFactory(any(Supplier.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        bookingClient = new BookingClient("http://localhost:8080", restTemplateBuilder);
    }

    @Test
    void shouldGetBookings() {
        long userId = 1L;
        State state = State.ALL;
        int from = 0;
        int size = 10;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                any(Map.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookings(userId, state, from, size);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(), eq(Object.class), any(Map.class));
    }


    @Test
    void shouldApproveBooking() {
        long userId = 1L;
        long bookingId = 2L;
        boolean isApproved = true;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.PATCH),
                any(),
                eq(Object.class),
                anyMap()
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.approveBooking(userId, bookingId, isApproved);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.PATCH), any(), eq(Object.class), anyMap());
    }

    @Test
    void shouldGetAllUserBookings() {
        long userId = 1L;
        State state = State.ALL;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                anyMap()
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllUserBooking(userId, state);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }

    @Test
    void shouldGetAllOwnerBookings() {
        long ownerId = 1L;
        State state = State.ALL;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class),
                anyMap()
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getAllOwnerBooking(ownerId, state);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(), eq(Object.class), anyMap());
    }

    @Test
    void shouldGetBookingById() {
        long userId = 1L;
        long bookingId = 2L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.GET),
                any(),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.getBookingById(bookingId, userId);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.GET), any(), eq(Object.class));
    }

    @Test
    void shouldAddNewBooking() {
        long userId = 1L;
        BookItemRequestDto bookItemRequestDto = new BookItemRequestDto();

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = bookingClient.addNewBooking(userId, bookItemRequestDto);

        assertEquals(expectedResponse, response);
        verify(restTemplate, times(1)).exchange(anyString(),
                eq(HttpMethod.POST), any(), eq(Object.class));
    }
}

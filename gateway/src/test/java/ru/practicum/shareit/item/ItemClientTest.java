package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);

        Mockito.when(builder.uriTemplateHandler(Mockito.any()))
                .thenReturn(builder);
        Mockito.when(builder.requestFactory(Mockito.<Supplier<ClientHttpRequestFactory>>any()))
                .thenReturn(builder);
        Mockito.when(builder.build())
                .thenReturn(restTemplate);

        itemClient = new ItemClient("http://localhost:8080", builder);
    }


    @Test
    void testAddNewItem() {
        Long userId = 1L;
        ItemDtoRequest request = new ItemDtoRequest();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.addNewItem(userId, request);

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.POST),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void testGetItemById() {
        Long itemId = 1L;
        Long userId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getItemById(itemId, userId);

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void testMakeAndSendRequestWithError() {
        HttpStatusCodeException exception = Mockito.mock(HttpStatusCodeException.class);
        Mockito.when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenThrow(exception);

        ResponseEntity<Object> response = itemClient.getItemById(1L, 1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testUpdateItem() {
        Long userId = 1L;
        Long itemId = 2L;
        ItemDtoRequest request = new ItemDtoRequest();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.eq("/" + itemId),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.updateItem(userId, itemId, request);

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.eq("/" + itemId),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void testGetOwnerItems() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getOwnerItems(userId);

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

}
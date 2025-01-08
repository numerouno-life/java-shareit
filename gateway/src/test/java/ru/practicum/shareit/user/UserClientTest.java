package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);

        Mockito.when(builder.uriTemplateHandler(Mockito.any()))
                .thenReturn(builder);
        Mockito.when(builder.requestFactory(Mockito.<Supplier<ClientHttpRequestFactory>>any()))
                .thenReturn(builder);
        Mockito.when(builder.build())
                .thenReturn(restTemplate);

        userClient = new UserClient("http://localhost:8080", builder);
    }

    @Test
    void testGetUserByIdTest() {
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getAllUsersTest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.GET),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void updateTest() {
        Long userId = 1L;
        UserDtoRequest userDtoRequest = new UserDtoRequest();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.eq("/" + userId),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.update(userId, userDtoRequest);

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.PATCH),
                Mockito.any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void deleteTest() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        Mockito.when(restTemplate.exchange(
                Mockito.eq("/" + userId),
                Mockito.eq(HttpMethod.DELETE),
                Mockito.any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.delete(userId);

        assertEquals(expectedResponse, response);
        Mockito.verify(restTemplate, Mockito.times(1)).exchange(
                Mockito.anyString(),
                Mockito.eq(HttpMethod.DELETE),
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

        ResponseEntity<Object> response = userClient.getUserById(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        RestTemplateBuilder builder = Mockito.mock(RestTemplateBuilder.class);

        when(builder.uriTemplateHandler(any()))
                .thenReturn(builder);
        when(builder.requestFactory(Mockito.<Supplier<ClientHttpRequestFactory>>any()))
                .thenReturn(builder);
        when(builder.build())
                .thenReturn(restTemplate);

        userClient = new UserClient("http://localhost:8080", builder);
    }

    @Test
    void testGetUserByIdTest() {
        Long userId = 1L;

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                anyString(),
                Mockito.eq(HttpMethod.GET),
                any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getUserById(userId);

        assertEquals(expectedResponse, response);
        verify(restTemplate, Mockito.times(1)).exchange(
                anyString(),
                Mockito.eq(HttpMethod.GET),
                any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void getAllUsersTest() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                anyString(),
                Mockito.eq(HttpMethod.GET),
                any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getAllUsers();

        assertEquals(expectedResponse, response);
        verify(restTemplate, Mockito.times(1)).exchange(
                anyString(),
                Mockito.eq(HttpMethod.GET),
                any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void updateTest() {
        Long userId = 1L;
        UserDtoRequest userDtoRequest = new UserDtoRequest();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                Mockito.eq("/" + userId),
                Mockito.eq(HttpMethod.PATCH),
                any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.update(userId, userDtoRequest);

        assertEquals(expectedResponse, response);
        verify(restTemplate, Mockito.times(1)).exchange(
                anyString(),
                Mockito.eq(HttpMethod.PATCH),
                any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void deleteTest() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(
                Mockito.eq("/" + userId),
                Mockito.eq(HttpMethod.DELETE),
                any(),
                Mockito.eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.delete(userId);

        assertEquals(expectedResponse, response);
        verify(restTemplate, Mockito.times(1)).exchange(
                anyString(),
                Mockito.eq(HttpMethod.DELETE),
                any(),
                Mockito.eq(Object.class)
        );
    }

    @Test
    void testMakeAndSendRequestWithError() {
        HttpStatusCodeException exception = Mockito.mock(HttpStatusCodeException.class);
        when(exception.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(restTemplate.exchange(
                anyString(),
                Mockito.eq(HttpMethod.GET),
                any(),
                Mockito.eq(Object.class)
        )).thenThrow(exception);

        ResponseEntity<Object> response = userClient.getUserById(1L);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void shouldSaveUserWhenEmailIsValid() {
        UserDtoRequest userDtoRequest = new UserDtoRequest();
        userDtoRequest.setName("Name");
        userDtoRequest.setEmail("name@mail.ru");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("Success");
        when(restTemplate.exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        )).thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.saveUser(userDtoRequest);

        assertEquals(expectedResponse, response);
        verify(restTemplate, Mockito.times(1)).exchange(
                eq(""),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(Object.class)
        );
    }

    @Test
    void shouldThrowIllegalArgumentExceptionWhenEmailIsInvalid() {
        UserDtoRequest userDtoRequest = new UserDtoRequest();
        userDtoRequest.setEmail("invalid_email");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userClient.saveUser(userDtoRequest);
        });
        assertEquals("Неверный формат e-mail", exception.getMessage());
        verify(restTemplate, never()).exchange(
                anyString(),
                any(HttpMethod.class),
                any(),
                any(Class.class)
        );
    }
}

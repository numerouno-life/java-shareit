package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserGatewayController.class)
public class UserGatewayControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserClient userClient;

    @SneakyThrows
    @Test
    void getUsers_shouldReturnOk() {
        when(userClient.getAllUsers()).thenReturn(ResponseEntity.ok(List.of()));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(userClient).getAllUsers();
    }

    @SneakyThrows
    @Test
    void getUserById_shouldReturnOk() {
        Long userId = 1L;
        String jsonResponse = "{\"id\":1,\"name\":\"UserName\",\"email\":\"user@example.com\"}";

        when(userClient.getUserById(userId)).thenReturn(ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse));

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonResponse));

        verify(userClient).getUserById(userId);
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserDtoRequest userDtoRequest = new UserDtoRequest(null, "John Doe", "john.doe@example.com");

        when(userClient.saveUser(any(UserDtoRequest.class)))
                .thenReturn(ResponseEntity.status(201).body("Created"));

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Created"));

        verify(userClient).saveUser(any(UserDtoRequest.class));
    }

    @SneakyThrows
    @Test
    void updateUser_shouldReturnOk() {
        Long userId = 1L;
        UserDtoRequest userDtoRequest =
                new UserDtoRequest(null, "Updated Name", "updated.email@example.com");

        when(userClient.update(eq(userId), any(UserDtoRequest.class)))
                .thenReturn(ResponseEntity.ok("Updated"));

        mvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated"));

        verify(userClient).update(eq(userId), any(UserDtoRequest.class));
    }

    @SneakyThrows
    @Test
    void deleteUser_shouldReturnOk() {
        Long userId = 1L;

        when(userClient.delete(userId)).thenReturn(ResponseEntity.ok("Deleted"));

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted"));

        verify(userClient).delete(userId);
    }

    @SneakyThrows
    @Test
    void createUserWithoutNameTest() {
        UserDtoRequest userDtoRequest = UserDtoRequest.builder()
                .email("test@test.ru")
                .build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void createUserWithoutEmailTest() {
        UserDtoRequest userDtoRequest = UserDtoRequest.builder()
                .name("Test User")
                .build();

        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isBadRequest());
    }
}

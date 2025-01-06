package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.utility.Update;


@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserGatewayController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        log.info("Ищем пользователя с id = {}", userId);
        return userClient.getUserById(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserDtoRequest userDtoRequest) {
        log.info("запрос на создание пользователя {}", userDtoRequest);
        return userClient.saveUser(userDtoRequest);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId,
                                             @RequestBody @Validated(Update.class) UserDtoRequest userDtoRequest) {
        log.info("запрос на обновление пользователя {}", userDtoRequest);
        return userClient.update(userId, userDtoRequest);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        return userClient.delete(userId);
    }

}

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:6541/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;

    @Test
    void testCanCreateUser() {
        UserDto user = userService.saveUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        assertEquals("Name", user.getName());
    }

    @Test
    void testCanGetUserById() {
        UserDto user = userService.saveUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        UserDto user1 = userService.getUserById(user.getId());
        assertEquals(user.getEmail(), user1.getEmail());
    }

    @Test
    void testThrowNotFoundIfGetNotExistUser() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void testCanUpdateUser() {
        UserDto user = userService.saveUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        userService.update(user.getId(), UserDto.builder().name("newName").email("newname@mail.ru").build());
        UserDto updatedUser = userService.getUserById(user.getId());
        assertEquals("newName", updatedUser.getName());
        assertEquals("newname@mail.ru", updatedUser.getEmail());
    }

    @Test
    void testCanUpdateUserName() {
        UserDto user = userService.saveUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        userService.update(user.getId(), UserDto.builder().name("newName").build());
        UserDto updatedUser = userService.getUserById(user.getId());
        assertEquals("newName", updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
    }

    @Test
    void testCanUpdateUserEmail() {
        UserDto user = userService.saveUser(UserDto.builder().name("Name").email("newname@mail.ru").build());
        userService.update(user.getId(), UserDto.builder().name("Name").build());
        UserDto updatedUser = userService.getUserById(user.getId());
        assertEquals(user.getName(), updatedUser.getName());
        assertEquals("newname@mail.ru", updatedUser.getEmail());
    }

    @Test
    void testCanDeleteUserById() {
        UserDto user = userService.saveUser(UserDto.builder().name("Name").email("name@mail.ru").build());
        userService.delete(user.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void testThrowNotFoundIfTryDeleteNotExistenceUser() {
        assertThrows(NotFoundException.class, () -> userService.getUserById(1L));
    }
}

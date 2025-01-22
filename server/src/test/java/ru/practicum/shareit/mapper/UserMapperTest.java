package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UserMapperTest {
    @InjectMocks
    private UserMapperImpl mapper;

    private User user;
    private UserDto userDto;
    private UserDtoShort userDtoShort;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@mail.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("Test User")
                .email("test@mail.com")
                .build();

        userDtoShort = UserDtoShort.builder()
                .id(1L)
                .name("Test User")
                .build();

    }

    @Test
    void toUserDto_shouldMapCorrectly() {
        UserDto result = mapper.toUserDto(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void toUserDtoShort_shouldMapCorrectly() {
        UserDtoShort result = mapper.toUserDtoShort(user);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
    }

    @Test
    void toUser_shouldMapCorrectly() {
        User result = mapper.toUser(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
    }

    @Test
    void toUser_shouldReturnNullForNullInput() {
        User result = mapper.toUser(null);

        assertEquals(null, result);
    }

    @Test
    void toUserDtoShort_shouldReturnNullForNullInput() {
        UserDtoShort result = mapper.toUserDtoShort(null);

        assertEquals(null, result);
    }

    @Test
    void toUserDto_shouldReturnNullForNullInput() {
        UserDto result = mapper.toUserDto(null);

        assertEquals(null, result);
    }
}

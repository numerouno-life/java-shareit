package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private final long id = 1L;

    private final User user = User.builder()
            .id(id)
            .name("name")
            .email("email@mail.ru")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(id)
            .name("name")
            .email("email@mail.ru")
            .build();

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(1)).toUserDto(user);
    }

    @Test
    void getUserById() {
        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));
        when(userRepository.findById(not(eq(user.getId())))).thenReturn(Optional.empty());
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(user.getId());
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getName(), result.getName());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(322L)
        );
        Assertions.assertEquals("Пользователь с ID:322 не найден", exception.getMessage());

        verify(userRepository, times(2)).findById(anyLong());
        verify(userMapper, times(1)).toUserDto(user);
    }

    @Test
    void saveUser_validUserDto_savesUserAndReturnsDto() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUser(any(UserDto.class))).thenReturn(user);
        when(userMapper.toUserDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.saveUser(userDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(userDto.getId(), result.getId());
        Assertions.assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void saveUser_invalidEmail_throwsIllegalArgumentException() {
        UserDto invalidUserDto = UserDto.builder()
                .id(2L)
                .name("Invalid User")
                .email("invalid-email")
                .build();

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> userService.saveUser(invalidUserDto)
        );

        Assertions.assertEquals("Неверный формат e-mail", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void update_existingUser_updatesFields() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userMapper.toUserDto(user)).thenReturn(userDto);

        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@mail.ru")
                .build();

        UserDto result = userService.update(user.getId(), updateDto);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Updated Name", user.getName());
        Assertions.assertEquals("updated@mail.ru", user.getEmail());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void update_nonExistingUser_throwsNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.update(999L, userDto)
        );

        Assertions.assertEquals("Пользователь с ID:999 не найден", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void delete_existingUser_deletesUser() {
        doNothing().when(userRepository).deleteById(user.getId());

        userService.delete(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void delete_nonExistingUser_noExceptionThrown() {
        doNothing().when(userRepository).deleteById(anyLong());

        Assertions.assertDoesNotThrow(() -> userService.delete(999L));

        verify(userRepository, times(1)).deleteById(999L);
    }

}

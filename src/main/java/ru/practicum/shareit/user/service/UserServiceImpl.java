package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> getUsers() {
        log.info("Возвращаем всех пользователей");
        return UserMapper.toUserDtoList(userStorage.getUsers());
    }

    @Override
    public UserDto getUserById(Integer userId) {
        log.info("Получаем пользователя по ID: {}", userId);
        return UserMapper.toUserDto(userStorage.getUserById(userId));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание пользователя {}", userDto);
        User user = UserMapper.toUser(userDto);
        validateUser(user);
        userStorage.createUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(Integer userId, UserDto userDto) {
        log.info("Обновление пользователя");
        User user = UserMapper.toUser(userDto);
        validateUser(user);
        user.setId(userId);
        userStorage.updateUser(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteUser(Integer userId) {
        log.info("Удаление пользователя по ID: {}", userId);
        return UserMapper.toUserDto(userStorage.deleteUser(userId));
    }

    private void validateUser(User user) {
        if (userStorage.getUsers().stream()
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new ConflictException("Пользователь с таким email уже существует");
        }
    }
}

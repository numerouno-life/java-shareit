package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long userId) {
        log.info("Получение пользователя с Id {}", userId);
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID:" + userId + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        log.info("Сохранение нового пользователя {}", userDto);
        if (!userDto.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new IllegalArgumentException("Неверный формат e-mail");

        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        log.info("Обновление пользователя {}", userDto);
        User user = UserMapper.toUser(getUserById(userId));
        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
        userRepository.save(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        log.info("Удаление пользователя с Id {}", userId);
        userRepository.deleteById(userId);
    }
}

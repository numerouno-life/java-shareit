package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private Integer currentId = 1;

    @Override
    public List<User> getUsers() {
        log.info("Возвращаем всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        log.info("Получаем пользователя по ID: {}", id);
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new NotFoundException("Пользователь с ID:" + id + " не найден");
    }

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя");
        if (user.getId() != null && users.containsKey(user.getId())) {
            log.error("Пользователь с ID:{} уже существует", user.getId());
            throw new IllegalArgumentException("Пользователь с таким ID уже существует");
        }
        user.setId(currentId++);
        users.put(user.getId(), user);
        log.info("Пользователь создан с ID: {}", user.getId());
        return user;
    }

    @Override
    public User updateUser(User updateUser) {
        log.info("Попытка обновления пользователя");
        User user = users.get(updateUser.getId());
        Optional<User> userOptionalUpdate = Optional.of(updateUser);
        userOptionalUpdate.map(User::getName).ifPresent(user::setName);
        userOptionalUpdate.map(User::getEmail).ifPresent(user::setEmail);
        users.put(user.getId(), user);
        log.info("Пользователь успешно изменен");
        return user;
    }

    @Override
    public User deleteUser(Integer userId) {
        log.info("Удаление пользователя по ID: {}", userId);
        User user = users.remove(userId);
        if (user == null) {
            log.error("Ошибка удаления. Пользователь с таким ID:{} не найден", userId);
            throw new NotFoundException("Пользователь с ID:" + userId + " не найден");
        }
        return user;
    }
}

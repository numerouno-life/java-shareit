package ru.practicum.shareit.user.repository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getUsers();

    User getUserById(Integer id);

    User createUser(User user);

    User updateUser(User user);

    User deleteUser(Integer userId);
}

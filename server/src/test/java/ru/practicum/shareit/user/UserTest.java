package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserTest {

    @Autowired
    private UserRepository userRepository;

    private User user = User.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .build();

    @Test
    @Transactional
    @Rollback
    void testCreateAndReadUser() {

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("John Doe");
        assertThat(savedUser.getEmail()).isEqualTo("john.doe@example.com");

        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();
        assertThat(foundUser.getName()).isEqualTo(savedUser.getName());
        assertThat(foundUser.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @Transactional
    @Rollback
    void testUpdateUser() {

        User savedUser = userRepository.save(user);
        savedUser.setName("New name");
        savedUser.setEmail("new.email@mail.com");
        User updateUser = userRepository.save(savedUser);

        assertThat(updateUser.getName()).isEqualTo("New name");
        assertThat(updateUser.getEmail()).isEqualTo("new.email@mail.com");

    }

    @Test
    @Transactional
    @Rollback
    void testDeleteUser() {

        User savedUser = userRepository.save(user);
        userRepository.deleteById(savedUser.getId());
        assertThat(userRepository.findById(savedUser.getId())).isEmpty();

    }
}
package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRequestEntityTest {

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();
        user = userRepository.save(user);
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test description")
                .requestor(user)
                .created(LocalDateTime.of(2023, 1, 1, 1, 1))
                .build();
        itemRequest = itemRequestRepository.save(itemRequest);
    }

    @Test
    void testSaveAndRetrieveItemRequest() {
        assertNotNull(itemRequest.getId());
        assertEquals("Test description", itemRequest.getDescription());
        assertEquals(user, itemRequest.getRequestor());

        ItemRequest retrievedItemRequest = itemRequestRepository.findById(itemRequest.getId()).orElseThrow();
        assertEquals(itemRequest, retrievedItemRequest);
    }

    @Test
    void testLazyLoadingRequestor() {
        itemRequestRepository.findAll().forEach(ir -> {
            assertNotNull(ir.getRequestor());
        });
    }

    @Test
    void testGetRequestByInvalidId() {
        long invalidId = 999L;
        Exception exception = assertThrows(Exception.class, () -> {
            itemRequestRepository.findById(invalidId).orElseThrow();
        });

        String expectedMessage = "No value present";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}

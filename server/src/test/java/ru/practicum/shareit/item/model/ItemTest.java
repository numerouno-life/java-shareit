package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Item item;
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
                .description("Test description itemRequest")
                .requestor(user)
                .created(LocalDateTime.of(2023, 1, 1, 1, 1))
                .build();
        itemRequest = itemRequestRepository.save(itemRequest);

        item = Item.builder()
                .id(1L)
                .name("Test item")
                .description("Test description item")
                .owner(user)
                .available(true)
                .request(itemRequest)
                .build();
        item = itemRepository.save(item);
    }

    @Test
    void testSaveAndRetrieveItem() {
        assertNotNull(item.getId());
        assertEquals("Test item", item.getName());
        assertEquals("Test description item", item.getDescription());
        assertEquals(user, item.getOwner());
        assertTrue(item.getAvailable());
        assertEquals(itemRequest, item.getRequest());

        Item retrievedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(item, retrievedItem);
    }

    @Test
    void testLazyLoadingOwnerAndRequest() {
        itemRepository.findAll().forEach(i -> {
            assertNotNull(i.getOwner());
            assertNotNull(i.getRequest());
        });
    }

    @Test
    void testGetItemByInvalidId() {
        long invalidId = 999L;
        Exception exception = assertThrows(Exception.class, () -> {
            itemRepository.findById(invalidId).orElseThrow();
        });

        String expectedMessage = "No value present";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void updateItemTest() {
        item.setName("Updated item");
        item.setDescription("Updated description item");
        item.setAvailable(false);

        itemRepository.save(item);

        Item retrievedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals("Updated item", retrievedItem.getName());
        assertEquals("Updated description item", retrievedItem.getDescription());
        assertFalse(retrievedItem.getAvailable());
        assertEquals(itemRequest, retrievedItem.getRequest());
    }

    @Test
    void deleteItemTest() {
        itemRepository.deleteById(item.getId());
        assertThrows(Exception.class, () -> itemRepository.findById(item.getId()).orElseThrow());
    }

    @Test
    void findByNameOrDescriptionTest() {
        String text = "Test";
        Iterable<Item> itemsByName = itemRepository.search(text);

        assertTrue(itemsByName.iterator().hasNext());
        itemsByName.forEach(i -> {
            assertTrue(i.getName().contains(text) || i.getDescription().contains(text));
        });
    }

    @Test
    void testWithoutRequiredFields() {
        Item invalidItem = Item.builder()
                .description("No description")
                .available(true)
                .owner(user)
                .build();

        assertThrows(Exception.class, () -> itemRepository.save(invalidItem));
    }

    @Test
    void testAssociationsWithOwnerAndRequest() {
        assertNotNull(item.getOwner());
        assertEquals(user, item.getOwner());
        assertNotNull(item.getRequest());
        assertEquals(itemRequest, item.getRequest());
    }

    @Test
    void testEqualsAndHashCode() {
        Item anotherItem = Item.builder()
                .id(item.getId())
                .name("Test item")
                .description("Test description item")
                .owner(user)
                .available(true)
                .request(itemRequest)
                .build();

        assertEquals(item, anotherItem);
        assertEquals(item.hashCode(), anotherItem.hashCode());

        anotherItem.setId(2L);
        assertNotEquals(item, anotherItem);
        assertNotEquals(item.hashCode(), anotherItem.hashCode());
    }

}

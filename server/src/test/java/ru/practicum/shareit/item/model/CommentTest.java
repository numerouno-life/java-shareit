package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Item item;
    private User user;
    private Comment comment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();
        user = userRepository.save(user);

        item = Item.builder()
                .id(1L)
                .name("Test item")
                .description("Test description item")
                .owner(user)
                .available(true)
                .build();
        item = itemRepository.save(item);

        comment = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .created(LocalDateTime.now().minusDays(1))
                .item(item)
                .build();
        comment = commentRepository.save(comment);
    }

    @Test
    void testSaveAndRetrieveComment() {
        assertNotNull(comment.getId());
        assertEquals("Test comment", comment.getText());
        assertEquals(user, comment.getAuthor());
        assertEquals(item, comment.getItem());

        Comment retrievedComment = commentRepository.findById(comment.getId()).orElseThrow();
        assertEquals(comment, retrievedComment);
    }

    @Test
    void testLazyLoadingAuthorAndItem() {
        commentRepository.findAll().forEach(c -> {
            assertNotNull(c.getAuthor());
            assertEquals(user, c.getAuthor());
            assertNotNull(c.getItem());
            assertEquals(item, c.getItem());
        });
    }

    @Test
    void testGetCommentByInvalidId() {
        long invalidId = 999L;
        Exception exception = assertThrows(Exception.class, () -> {
            itemRepository.findById(invalidId).orElseThrow();
        });

        String expectedMessage = "No value present";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDeleteComment() {
        commentRepository.deleteById(comment.getId());

        Exception exception = assertThrows(Exception.class, () -> {
            commentRepository.findById(comment.getId()).orElseThrow();
        });

        String expectedMessage = "No value present";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testWithOutRequiredFields() {
        Comment commentWithoutRequiredFields = Comment.builder()
                .build();

        assertThrows(Exception.class, () -> commentRepository.save(commentWithoutRequiredFields));
    }

    @Test
    void testAssociationsWithAuthorAndItem() {
        assertNotNull(comment.getAuthor());
        assertEquals(user, comment.getAuthor());
        assertNotNull(comment.getItem());
        assertEquals(item, comment.getItem());
    }

    @Test
    void testDeleteItemWithAssociatedComments() {
        commentRepository.deleteById(comment.getId());
        itemRepository.deleteById(item.getId());

        assertFalse(itemRepository.existsById(item.getId()));
        assertFalse(commentRepository.existsById(comment.getId()));
    }

    @Test
    void testEqualsAndHashCode() {
        Comment comment1 = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .created(LocalDateTime.now().minusDays(1))
                .item(item)
                .build();

        Comment comment2 = Comment.builder()
                .id(1L)
                .text("Test comment")
                .author(user)
                .created(LocalDateTime.now().minusDays(1))
                .item(item)
                .build();

        assertEquals(comment1, comment2);
        assertEquals(comment1.hashCode(), comment2.hashCode());
    }
}
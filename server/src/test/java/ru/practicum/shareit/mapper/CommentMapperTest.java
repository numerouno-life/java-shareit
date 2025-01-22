package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapperImpl;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class CommentMapperTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CommentMapperImpl mapper;

    private User user;
    private Item item;
    private Comment comment;
    private CommentDtoIn commentDtoIn;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@mail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(user)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Test Comment")
                .item(item)
                .author(user)
                .build();

        commentDtoIn = CommentDtoIn.builder()
                .id(1L)
                .text("Test Comment")
                .build();
    }

    @Test
    void toCommentDtoOut_shouldMapCorrectly() {
        CommentDtoOut result = mapper.toCommentDtoOut(comment);

        assertNotNull(result);
        assertEquals(comment.getText(), result.getText());
        assertEquals(comment.getAuthor().getName(), result.getAuthorName());
        assertEquals(comment.getCreated(), result.getCreated());
    }

    @Test
    void toComment_shouldMapCorrectly() {
        Comment result = mapper.toComment(commentDtoIn, item, user);

        assertNotNull(result);
        assertEquals(commentDtoIn.getText(), result.getText());
        assertEquals(item, result.getItem());
        assertEquals(user, result.getAuthor());
    }

    @Test
    void testToCommentDtoOut_Null() {
        CommentDtoOut result = mapper.toCommentDtoOut(null);
        assertNull(result);
    }

    @Test
    void testToComment_textNull() {
        Comment result = mapper.toComment(null, item, user);
        assertNull(result.getText());
    }

    @Test
    void testToComment_AllNull() {
        Comment result = mapper.toComment(null, null, null);
        assertNull(result);
    }
}

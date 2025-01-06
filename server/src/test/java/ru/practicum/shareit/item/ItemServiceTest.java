package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private Booking booking;
    private Comment comment;
    private CommentDtoOut commentDtoOut;
    private ItemRequest itemRequest;
    private ItemDtoIn itemDtoIn;
    private ItemDtoOut itemDtoOut;
    private UserDtoShort userDtoShort;
    private CommentDtoIn commentDtoIn;


    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test item name")
                .description("Test item description")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Booking.BookingStatus.APPROVED)
                .build();

        comment = Comment.builder()
                .id(1L)
                .author(user)
                .text("Test comment")
                .build();

        commentDtoOut = CommentDtoOut.builder()
                .id(1L)
                .text("Test comment")
                .authorName(user.getName())
                .created(LocalDateTime.now().minusDays(2))
                .build();

        commentDtoIn = CommentDtoIn.builder()
                .id(1L)
                .text("Test comment")
                .build();

        userDtoShort = UserDtoShort.builder()
                .id(1L)
                .name("User")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test description")
                .requestor(user)
                .created(LocalDateTime.of(2023, 1, 1, 1, 1))
                .build();

        itemDtoIn = ItemDtoIn.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();

        itemDtoOut = ItemDtoOut.builder()
                .id(1L)
                .name("Test name")
                .description("Test description")
                .available(true)
                .requestId(itemRequest.getId())
                .owner(userDtoShort)
                .build();
    }

    @Test
    void addNewItem_ShouldReturnCreatedItem() {
        mockUserRepositoryFindById();
        mockItemRequestRepositoryFindById();
        mockMapItemDtoToItem();
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        mockToItemDtoOut();

        ItemDtoOut result = itemService.addNewItem(1L, itemDtoIn);

        assertionsFields(itemDtoOut, result);

        verify(itemMapper).mapItemDtoToItem(any(ItemDtoIn.class), any(User.class), any(ItemRequest.class));
        verify(itemRequestRepository).findById(1L);
        verify(itemRepository).save(item);
        verify(itemMapper).toItemDtoOut(item);
    }

    @Test
    void getItemById_ShouldReturnItem() {
        mockItemRepositoryFindById();
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));
        when(itemMapper.toItemDtoOut(any(Item.class))).thenReturn(itemDtoOut);

        ItemDtoOut result = itemService.getItemById(1L, 1L);

        assertionsFields(itemDtoOut, result);

        verify(itemRepository).findById(1L);
        verify(itemMapper).toItemDtoOut(item);
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() {
        mockUserRepositoryFindById();
        mockItemRepositoryFindById();

        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toItemDtoOut(any(Item.class))).thenReturn(itemDtoOut);

        ItemDtoOut result = itemService.updateItem(1L, 1L, itemDtoIn);

        assertionsFields(itemDtoOut, result);

        verify(itemRepository).findById(1L);
        verify(userRepository).findById(1L);
        verify(itemRepository).save(item);
        verify(itemMapper).toItemDtoOut(item);
    }

    @Test
    void getOwnerItems_ShouldReturnOwnerItems() {
        mockUserRepositoryFindById();
        when(itemRepository.findAllByOwnerId(any(Long.class))).thenReturn(List.of(item));
        mockToItemDtoOut();

        List<ItemDtoOut> result = itemService.getOwnerItems(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertionsFields(itemDtoOut, result.get(0));

        verify(userRepository).findById(1L);
        verify(itemRepository).findAllByOwnerId(1L);
        verify(itemMapper).toItemDtoOut(item);
    }

    @Test
    void searchItemByText_ShouldReturnFoundItems() {
        when(itemRepository.search(any(String.class))).thenReturn(List.of(item));
        mockToItemDtoOut();

        List<ItemDtoOut> result = itemService.searchItemByText("Test");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertionsFields(itemDtoOut, result.get(0));

        verify(itemRepository).search("Test");
        verify(itemMapper).toItemDtoOut(item);
    }

    @Test
    void saveComment_ShouldSaveCommentSuccessfully() {
        when(itemRepository.existsById(item.getId())).thenReturn(true);
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(user.getId()),
                eq(item.getId()),
                eq(Booking.BookingStatus.APPROVED),
                any(LocalDateTime.class)))
                .thenReturn(booking);
        when(commentMapper.toComment(any(CommentDtoIn.class), eq(item), eq(user))).thenReturn(comment);
        mockCommentRepositorySave();
        when(commentMapper.toCommentDtoOut(any(Comment.class))).thenReturn(commentDtoOut);

        CommentDtoOut result = itemService.saveComment(1L, commentDtoIn, 1L);

        assertNotNull(result);
        assertEquals(commentDtoOut.getId(), result.getId());
        assertEquals(commentDtoOut.getAuthorName(), result.getAuthorName());
        assertEquals(commentDtoOut.getText(), result.getText());
        assertEquals(commentDtoOut.getCreated(), result.getCreated());

        verify(itemRepository).existsById(item.getId());
        verify(userRepository).existsById(user.getId());
        verify(bookingRepository).findByBookerIdAndItemIdAndStatusAndEndBefore(
                eq(user.getId()),
                eq(item.getId()),
                eq(Booking.BookingStatus.APPROVED),
                any(LocalDateTime.class));
        verify(commentRepository).save(comment);
    }

    @Test
    void getItemById_ShouldThrowNotFoundException_WhenItemDoesNotExist() {
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemById(999L, 1L)
        );

        assertEquals("Вещь с Id 999 не найдена", exception.getMessage());
        verify(itemRepository).findById(999L);
    }

    @Test
    void addNewItem_ShouldThrowValidationException_WhenNameIsEmpty() {
        itemDtoIn.setName("");
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.addNewItem(1L, itemDtoIn)
        );

        assertEquals("Имя вещи не может быть пустым", exception.getMessage());
    }


    private void mockUserRepositoryFindById() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
    }

    private void mockItemRepositoryFindById() {
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));
    }

    private void mockToItemDtoOut() {
        when(itemMapper.toItemDtoOut(any(Item.class))).thenReturn(itemDtoOut);
    }

    private void mockMapItemDtoToItem() {
        when(itemMapper.mapItemDtoToItem(
                any(ItemDtoIn.class), any(User.class), any(ItemRequest.class))).thenReturn(item);
    }

    private void mockItemRequestRepositoryFindById() {
        when(itemRequestRepository.findById(any(Long.class))).thenReturn(Optional.of(itemRequest));
    }

    private void mockCommentRepositorySave() {
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder()
                .id(1L)
                .item(item)
                .author(user)
                .build());
    }

    private void assertionsFields(ItemDtoOut itemDtoOut, ItemDtoOut result) {
        assert result.equals(itemDtoOut);
        assertEquals(itemDtoOut.getId(), result.getId());
        assertEquals(itemDtoOut.getName(), result.getName());
        assertEquals(itemDtoOut.getDescription(), result.getDescription());
        assertEquals(itemDtoOut.getAvailable(), result.getAvailable());
        assertEquals(itemDtoOut.getRequestId(), result.getRequestId());
        assertEquals(itemDtoOut.getOwner(), result.getOwner());
    }
}

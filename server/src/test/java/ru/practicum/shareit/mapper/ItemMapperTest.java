package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ItemMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemMapperImpl mapper;

    private User user;
    private Item item;
    private ItemDtoIn itemDtoIn;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@mail.com")
                .build();

        itemRequest = new ItemRequest();
        itemRequest.setId(2L);

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(user)
                .request(itemRequest)
                .build();

        itemDtoIn = ItemDtoIn.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(userMapper.toUserDtoShort(user)).thenReturn(new UserDtoShort(user.getId(), user.getName()));
    }

    @Test
    void toItemDtoOut_shouldMapCorrectly() {
        ItemDtoOut result = mapper.toItemDtoOut(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequest().getId(), result.getRequestId());
        assertEquals(user.getId(), result.getOwner().getId());
    }

    @Test
    void toItemDto_shouldMapCorrectly() {
        ItemDto result = mapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void toItem_shouldMapCorrectly() {
        Item result = mapper.toItem(itemDtoIn);

        assertNotNull(result);
        assertEquals(itemDtoIn.getName(), result.getName());
        assertEquals(itemDtoIn.getDescription(), result.getDescription());
        assertEquals(itemDtoIn.getAvailable(), result.getAvailable());
    }

    @Test
    void mapItemDtoToItem_shouldMapCorrectly() {
        Item result = mapper.mapItemDtoToItem(itemDtoIn, user, itemRequest);

        assertNotNull(result);
        assertEquals(itemDtoIn.getId(), result.getId());
        assertEquals(itemDtoIn.getName(), result.getName());
        assertEquals(itemDtoIn.getDescription(), result.getDescription());
        assertEquals(itemDtoIn.getAvailable(), result.getAvailable());
        assertEquals(user, result.getOwner());
        assertEquals(itemRequest, result.getRequest());
    }

    @Test
    void testMapToItemDtoOut_Null() {
        ItemDtoOut result = mapper.toItemDtoOut(null);
        assertNull(result);
    }

    @Test
    void testMapToItemDto_Null() {
        ItemDto result = mapper.toItemDto(null);
        assertNull(result);
    }

    @Test
    void testMapToItem_Null() {
        Item result = mapper.toItem(null);
        assertNull(result);
    }

    @Test
    void testMapItemDtoToItem_Null() {
        Item result = mapper.mapItemDtoToItem(null, null, null);
        assertNull(result);
    }

    @Test
    void toItemDtoOut_shouldHandleNullRequest() {
        item.setRequest(null);
        ItemDtoOut result = mapper.toItemDtoOut(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertNull(result.getRequestId());
    }

    @Test
    void mapItemDtoToItem_shouldHandleNullUser() {
        Item result = mapper.mapItemDtoToItem(itemDtoIn, null, itemRequest);

        assertNotNull(result);
        assertEquals(itemDtoIn.getName(), result.getName());
        assertEquals(itemDtoIn.getDescription(), result.getDescription());
        assertEquals(itemDtoIn.getAvailable(), result.getAvailable());
        assertNull(result.getOwner());
    }

    @Test
    void toItemDto_shouldHandleNullFields() {
        item.setName(null);
        item.setDescription(null);
        item.setAvailable(null);
        ItemDto result = mapper.toItemDto(item);

        assertNotNull(result);
        assertNull(result.getName());
    }

    @Test
    void mapItemDtoToItem_shouldHandleNullRequest() {
        Item result = mapper.mapItemDtoToItem(itemDtoIn, user, null);

        assertNotNull(result);
        assertEquals(itemDtoIn.getName(), result.getName());
        assertEquals(user, result.getOwner());
        assertNull(result.getRequest());
    }

}

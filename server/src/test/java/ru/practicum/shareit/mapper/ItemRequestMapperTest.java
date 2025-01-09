package ru.practicum.shareit.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;

public class ItemRequestMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemRequestMapperImpl mapper;

    private ItemRequest itemRequest;
    private ItemRequestDtoOut itemRequestDtoOut;
    private ItemRequestDtoIn itemRequestDtoIn;
    private User user;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@mail.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("IR Description")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        itemRequestDtoOut = ItemRequestDtoOut.builder()
                .id(1L)
                .description("IR Description")
                .requestorId(1L)
                .created(LocalDateTime.now())
                .build();

        itemRequestDtoIn = ItemRequestDtoIn.builder()
                .description("New IR Description")
                .build();


    }

    @Test
    void toRequestDtoOut_shouldMapCorrectly() {
        ItemRequestDtoOut result = mapper.toRequestDtoOut(itemRequest);

        assertNotNull(result);
        assertEquals(itemRequest.getId(), result.getId());
        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(itemRequest.getCreated(), result.getCreated());
    }

    @Test
    void toRequest_shouldMapCorrectly() {
        ItemRequest result = mapper.toRequest(itemRequestDtoIn);

        assertNotNull(result);
        assertEquals(itemRequestDtoIn.getDescription(), result.getDescription());
    }

    @Test
    void mapItemRequsetDtoToItem_shouldMapCorrectly() {
        ItemRequest result = mapper.mapItemRequsetDtoToItem(itemRequestDtoIn, user);

        assertNotNull(result);
        assertEquals(itemRequestDtoIn.getDescription(), result.getDescription());
        assertEquals(user, result.getRequestor());
    }

    @Test
    void toRequestDtoOut_shouldReturnNullForNullInput() {
        ItemRequestDtoOut result = mapper.toRequestDtoOut(null);

        assertEquals(result, null);
    }

    @Test
    void toRequest_shouldReturnNullForNullInput() {
        ItemRequest result = mapper.toRequest(null);

        assertEquals(result, null);
    }

    @Test
    void mapItemRequsetDtoToItem_shouldReturnNullForNullInput() {
        ItemRequest result = mapper.mapItemRequsetDtoToItem(null, null);

        assertEquals(result, null);
    }

    @Test
    void mapItemRequsetDtoToItem_shouldReturnNullForNullUser() {
        ItemRequest result = mapper.mapItemRequsetDtoToItem(itemRequestDtoIn, null);

        assertNull(null, result.getRequestor());
    }
}

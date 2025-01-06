package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDtoOut itemRequestDtoOut;
    private ItemRequestDtoIn itemRequestDtoIn;

    @BeforeEach
    void setup() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test description")
                .requestor(user)
                .created(LocalDateTime.of(2023, 1, 1, 1, 1))
                .build();
        itemRequestDtoOut = ItemRequestDtoOut.builder()
                .id(1L)
                .description("Test description")
                .requestorId(1L)
                .created(LocalDateTime.of(2023, 1, 1, 1, 1))
                .build();
        itemRequestDtoIn = ItemRequestDtoIn.builder()
                .description("New request")
                .build();
    }

    @Test
    void addNewRequest_ShouldReturnSavedRequest() {
        mockUserRepositoryFindById();
        mockItemRequestMapperToRequest();
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        mockItemRequestMapperToRequestDtoOut();

        ItemRequestDtoOut result = itemRequestService.addNewRequest(1L, itemRequestDtoIn);

        assertNotNull(result);
        assertEquals(itemRequestDtoOut.getId(), result.getId());
        assertEquals(itemRequestDtoOut.getDescription(), result.getDescription());
    }

    @Test
    void getOwnerRequests_ShouldReturnRequestsWithItems() {
        mockUserRepositoryFindById();
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));
        mockItemRepositoryFindAllByRequestId();
        mockItemRequestMapperToRequestDtoOut();
        mockItemMapperToItemDtoOut();

        List<ItemRequestDtoOut> result = itemRequestService.getOwnerRequests(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDtoOut.getId(), result.get(0).getId());
        assertEquals(itemRequestDtoOut.getDescription(), result.get(0).getDescription());
    }

    @Test
    void getAllRequests_ShouldReturnRequestsWithItems() {
        mockUserRepositoryFindById();
        when(itemRequestRepository.findAllByRequestorIdIsNot(eq(1L), any(Pageable.class))).thenReturn(List.of(itemRequest));
        mockItemRepositoryFindAllByRequestId();
        mockItemRequestMapperToRequestDtoOut();
        mockItemMapperToItemDtoOut();

        List<ItemRequestDtoOut> result = itemRequestService.getAllRequests(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRequestByIdWithItems_ShouldReturnRequestWithItems() {
        mockUserRepositoryFindById();
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        mockItemRequestMapperToRequestDtoOut();

        ItemRequestDtoOut result = itemRequestService.getRequestByIdWithItems(1L, 1L);

        assertNotNull(result);
        assertEquals(itemRequestDtoOut.getId(), result.getId());
        assertEquals(itemRequestDtoOut.getDescription(), result.getDescription());
    }

    @Test
    void testGetRequestByIdUserNotFound() {
        long invalidUserId = 322L;

        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestByIdWithItems(invalidUserId, 1L));

        assertEquals("Пользователь с ID " + invalidUserId + " не найден", exception.getMessage());

        verify(userRepository).findById(invalidUserId);
        verifyNoInteractions(itemRequestRepository);
    }

    @Test
    void testGetRequestByIdRequestNotFound() {
        long validUserId = 1L;
        long invalidRequestId = 322L;

        when(userRepository.findById(validUserId)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(invalidRequestId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestByIdWithItems(validUserId, invalidRequestId));

        assertEquals("Запрос с ID " + invalidRequestId + " не найден", exception.getMessage());

        verify(userRepository).findById(validUserId);
        verify(itemRequestRepository).findById(invalidRequestId);
    }

    @Test
    void testCreateRequestForNonExistentUser() {
        long invalidUserId = 322L;
        ItemRequestDtoIn requestDtoIn = ItemRequestDtoIn.builder().build();
        requestDtoIn.setDescription("Garlic");

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemRequestService.addNewRequest(invalidUserId, requestDtoIn));
        assertEquals("Пользователь с ID " + invalidUserId + " не найден", exception.getMessage());
    }

    private void mockUserRepositoryFindById() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
    }

    private void mockItemRequestMapperToRequest() {
        when(itemRequestMapper.toRequest(any(ItemRequestDtoIn.class))).thenReturn(itemRequest);
    }

    private void mockItemRequestMapperToRequestDtoOut() {
        when(itemRequestMapper.toRequestDtoOut(any(ItemRequest.class))).thenReturn(itemRequestDtoOut);
    }

    private void mockItemRepositoryFindAllByRequestId() {
        when(itemRepository.findAllByRequestId(any(Long.class))).thenReturn(List.of(
                Item.builder()
                        .id(101L)
                        .name("Test Item")
                        .description("Description")
                        .available(true)
                        .owner(user)
                        .request(itemRequest)
                        .build()
        ));
    }

    private void mockItemMapperToItemDtoOut() {
        when(itemMapper.toItemDtoOut(any(Item.class))).thenReturn(
                ItemDtoOut.builder()
                        .id(101L)
                        .name("Test Item")
                        .description("Description")
                        .available(true)
                        .build()
        );
    }
}

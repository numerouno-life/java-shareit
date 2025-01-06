package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.AuthorizationException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private Booking booking;
    private BookingDtoOut bookingDtoOut;
    private BookingDtoIn bookingDtoIn;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("User")
                .email("user@mail.com")
                .build();

        userDto = UserDto.builder()
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
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test item name")
                .build();

        lenient().when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        booking = Booking.builder()
                .id(1L)
                .item(item)
                .booker(user)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Booking.BookingStatus.APPROVED)
                .build();

        bookingDtoOut = BookingDtoOut.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(Booking.BookingStatus.APPROVED)
                .state(State.ALL)
                .booker(userDto)
                .item(itemDto)
                .build();

        bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .itemId(1L)
                .build();
    }

    @Test
    void addNewBooking_ShouldReturnCreateBooking() {
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockUserRepositoryFindById();
            mockItemRepositoryFindById();
            mockSaveBooking();

            mockedMapper.when(() -> BookingMapper.toBooking(any(BookingDtoIn.class), any(User.class), any(Item.class), any(Booking.BookingStatus.class)))
                    .thenReturn(booking);
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class)))
                    .thenReturn(bookingDtoOut);

            BookingDtoOut result = bookingService.addNewBooking(1L, bookingDtoIn);

            assertionsFields(bookingDtoOut, result);
            assertEquals(bookingDtoOut.getId(), result.getId());
            assertEquals(bookingDtoOut.getStatus(), result.getStatus());

            verify(bookingRepository).save(any(Booking.class));
            verify(userRepository).findById(anyLong());
            verify(itemRepository).findById(anyLong());
        }
    }

    @Test
    void approveBooking_ShouldApproveBooking() {
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            booking.setStatus(Booking.BookingStatus.WAITING);
            mockBookingRepositoryFindById();
            mockSaveBooking();
            mockToBookingDtoOut();

            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class)))
                    .thenReturn(bookingDtoOut);

            BookingDtoOut result = bookingService.approveBooking(1L, 1L, true);

            assertionsFields(bookingDtoOut, result);

            verify(bookingRepository).findById(anyLong());
            verify(bookingRepository).save(any(Booking.class));
        }
    }

    @Test
    void approveBooking_ShouldThrowAuthorizationException_WhenBookingNotExists() {
        booking.setStatus(Booking.BookingStatus.APPROVED);
        item.setOwner(user);
        mockBookingRepositoryFindById();

        ValidationException exception =
                assertThrows(ValidationException.class,
                        () -> bookingService.approveBooking(user.getId(), booking.getId(), true));
        assertEquals("Бронирование уже обработано. Текущий статус: APPROVED", exception.getMessage());

    }

    @Test
    void getBookingById_ShouldReturnBooking() {
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockUserRepositoryFindById();
            mockBookingRepositoryFindById();
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            BookingDtoOut result = bookingService.getBookingById(booking.getId(), user.getId());

            assertionsFields(bookingDtoOut, result);

            verify(bookingRepository).findById(anyLong());
            verify(userRepository).findById(anyLong());
        }
    }

    @Test
    void getBookingById_ShouldThrowAuthorizationException_WhenUserNotOwner() {
        User anotherUser = User.builder()
                .id(2L)
                .name("Another user")
                .email("another@mail.com")
                .build();

        mockBookingRepositoryFindById();

        AuthorizationException exception = assertThrows(AuthorizationException.class,
                () -> bookingService.getBookingById(booking.getId(), anotherUser.getId()));

        assertEquals("Получить информацию о бронировании могут только владелец вещи или автор бронирования",
                exception.getMessage());
    }

    @Test
    void getAllUserBooking_ShouldReturnAllBookings() {
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockUserRepositoryFindById();
            when(bookingRepository.findByBookerId(anyLong())).thenReturn(new ArrayList<>(List.of(booking)));
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);
            List<BookingDtoOut> result = bookingService.getAllUserBooking(user.getId(), State.ALL);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));

            verify(bookingRepository).findByBookerId(anyLong());
            verify(userRepository).findById(anyLong());
        }
    }

    @Test
    void getAllUserBooking_ShouldReturnCurrentBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllUserBooking(user.getId(), State.CURRENT);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByBookerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    void getAllUserBooking_ShouldReturnPastBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByBookerIdAndEndBefore(anyLong(),
                any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllUserBooking(user.getId(), State.PAST);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByBookerIdAndEndBefore(anyLong(), any(LocalDateTime.class));
        }
    }

    @Test
    void getAllUserBooking_ShouldReturnFutureBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByBookerIdAndStartAfter(anyLong(),
                any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllUserBooking(user.getId(), State.FUTURE);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByBookerIdAndStartAfter(anyLong(), any(LocalDateTime.class));
        }
    }

    @Test
    void getAllUserBooking_ShouldReturnWaitingBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByBookerIdAndStatus(anyLong(),
                any(Booking.BookingStatus.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllUserBooking(user.getId(), State.WAITING);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByBookerIdAndStatus(anyLong(), any(Booking.BookingStatus.class));
        }
    }

    @Test
    void getAllUserBooking_ShouldReturnRejectedBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByBookerIdAndStatus(anyLong(),
                any(Booking.BookingStatus.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllUserBooking(user.getId(), State.REJECTED);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByBookerIdAndStatus(anyLong(), any(Booking.BookingStatus.class));
        }
    }

    @Test
    void getAllUserBooking_ShouldReturnIllegalArgumentException_thenStateUnknown() {

        mockUserRepositoryFindById();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getAllUserBooking(user.getId(), State.INVALID));

        assertEquals("Неизвестное состояние: INVALID", exception.getMessage());
    }

    @Test
    void getAllOwnerBooking_ShouldReturnAllBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByItemOwnerId(anyLong())).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllOwnerBooking(item.getId(), State.ALL);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByItemOwnerId(anyLong());
        }
    }

    @Test
    void getAllOwnerBooking_ShouldReturnCurrentBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(anyLong(), any(LocalDateTime.class),
                any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllOwnerBooking(item.getId(), State.CURRENT);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByItemOwnerIdAndStartBeforeAndEndAfter(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        }
    }

    @Test
    void getAllOwnerBooking_ShouldReturnPastBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByItemOwnerIdAndEndBefore(anyLong(),
                any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllOwnerBooking(item.getId(), State.PAST);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByItemOwnerIdAndEndBefore(anyLong(), any(LocalDateTime.class));
        }
    }

    @Test
    void getAllOwnerBooking_ShouldReturnFutureBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByItemOwnerIdAndStartAfter(anyLong(),
                any(LocalDateTime.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllOwnerBooking(item.getId(), State.FUTURE);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByItemOwnerIdAndStartAfter(anyLong(), any(LocalDateTime.class));
        }
    }

    @Test
    void getAllOwnerBooking_ShouldReturnWaitingBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(),
                any(Booking.BookingStatus.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllOwnerBooking(item.getId(), State.WAITING);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByItemOwnerIdAndStatus(anyLong(), any(Booking.BookingStatus.class));
        }
    }

    @Test
    void getAllOwnerBooking_ShouldReturnRejectedBookings() {
        mockUserRepositoryFindById();
        when(bookingRepository.findByItemOwnerIdAndStatus(anyLong(),
                any(Booking.BookingStatus.class))).thenReturn(new ArrayList<>(List.of(booking)));
        try (MockedStatic<BookingMapper> mockedMapper = Mockito.mockStatic(BookingMapper.class)) {
            mockedMapper.when(() -> BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);

            List<BookingDtoOut> result = bookingService.getAllOwnerBooking(item.getId(), State.REJECTED);

            assertEquals(1, result.size());
            assertionsFields(bookingDtoOut, result.get(0));
            verify(bookingRepository).findByItemOwnerIdAndStatus(anyLong(), any(Booking.BookingStatus.class));
        }
    }

    @Test
    void getAllOwnerBooking_ShouldReturnIllegalArgumentException_thenStateUnknown() {
        mockUserRepositoryFindById();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getAllOwnerBooking(user.getId(), State.INVALID));

        assertEquals("Неизвестное состояние: INVALID", exception.getMessage());
    }

    private void mockUserRepositoryFindById() {
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
    }

    private void mockItemRepositoryFindById() {
        when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));
    }

    private void mockBookingRepositoryFindById() {
        lenient().when(bookingRepository.findById(any(Long.class))).thenReturn(Optional.of(booking));
    }

    private void mockSaveBooking() {
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
    }

    private void mockToBookingDtoOut() {
        when(BookingMapper.toBookingDtoOut(any(Booking.class))).thenReturn(bookingDtoOut);
    }

    private void assertionsFields(BookingDtoOut expected, BookingDtoOut result) {
        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getStatus(), result.getStatus());
        assertEquals(expected.getState(), result.getState());
        assertEquals(expected.getBooker().getId(), result.getBooker().getId());
        assertEquals(expected.getItem().getId(), result.getItem().getId());
        assertEquals(expected.getStart(), result.getStart());
        assertEquals(expected.getEnd(), result.getEnd());
    }
}

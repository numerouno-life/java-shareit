package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BookingMapperTest {

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserMapper userMapper;

    private User booker = User.builder()
            .id(1L)
            .name("Booker name")
            .email("maip@mail.ru")
            .build();

    private Item item = Item.builder()
            .id(2L)
            .name("Item")
            .description("Description Item")
            .available(true)
            .owner(booker).build();

    private Booking booking = Booking.builder()
            .id(3L)
            .start(LocalDateTime.of(2021, 11, 11, 11, 11))
            .end(LocalDateTime.of(2022, 11, 11, 11, 11))
            .status(Booking.BookingStatus.APPROVED)
            .item(item)
            .booker(booker)
            .build();

    private ItemDto itemDto = ItemDto.builder()
            .id(2L)
            .name("Item")
            .build();

    private UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(booker.getId())
            .name(booker.getName())
            .build();

    private BookingDto bookingDto = BookingDto.builder()
            .id(4L)
            .start(LocalDateTime.of(2021, 11, 11, 11, 11))
            .end(LocalDateTime.of(2022, 11, 11, 11, 11))
            .bookerId(booker.getId())
            .status(Booking.BookingStatus.APPROVED)
            .build();

    @Test
    void toBookingDtoOutTest() {

        BookingDtoOut bookingDtoOut = BookingMapper.toBookingDtoOut(booking);

        assertEquals(booking.getId(), bookingDtoOut.getId());
        assertEquals(booking.getStart(), bookingDtoOut.getStart());
        assertEquals(booking.getEnd(), bookingDtoOut.getEnd());
        assertEquals(booking.getStatus(), bookingDtoOut.getStatus());

        assertEquals(itemDto.getId(), bookingDtoOut.getItem().getId());
        assertEquals(itemDto.getName(), bookingDtoOut.getItem().getName());

        assertEquals(userDtoShort.getId(), bookingDtoOut.getBooker().getId());
        assertEquals(userDtoShort.getName(), bookingDtoOut.getBooker().getName());
    }

    @Test
    void toBookingDtoTest() {
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }

    @Test
    void toBookingTest() {
        BookingDtoIn bookingDtoIn = BookingDtoIn.builder()
                .start(LocalDateTime.of(2023, 11, 11, 11, 11))
                .end(LocalDateTime.of(2023, 11, 12, 11, 11))
                .build();

        Booking toBooking = BookingMapper.toBooking(bookingDtoIn, booker, item, Booking.BookingStatus.APPROVED);

        assertEquals(bookingDtoIn.getStart(), toBooking.getStart());
        assertEquals(bookingDtoIn.getEnd(), toBooking.getEnd());
        assertEquals(Booking.BookingStatus.APPROVED, toBooking.getStatus());
        assertEquals(item, toBooking.getItem());
        assertEquals(booker, toBooking.getBooker());
    }

}
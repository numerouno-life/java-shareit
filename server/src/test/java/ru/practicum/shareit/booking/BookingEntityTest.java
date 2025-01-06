package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingEntityTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    private Item item;
    private User user;
    private Booking booking;

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

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(Booking.BookingStatus.APPROVED)
                .build();
        booking = bookingRepository.save(booking);
    }

    @Test
    void testSaveAndRetrieveItem() {
        assertNotNull(item.getId());
        assertEquals("Test item", item.getName());
        assertEquals("Test description item", item.getDescription());
        assertEquals(user, item.getOwner());
        assertTrue(item.getAvailable());

        Item retrievedItem = itemRepository.findById(item.getId()).orElseThrow();
        assertEquals(item, retrievedItem);
    }

    @Test
    void testLazyLoadingOwnerAndBooking() {
        bookingRepository.findAll().forEach(b -> {
            assertNotNull(b.getItem());
            assertNotNull(b.getBooker());
        });
    }

    @Test
    void testGetBookingByInvalidId() {
        long invalidId = 999L;
        Exception exception = assertThrows(Exception.class, () -> {
            bookingRepository.findById(invalidId).orElseThrow();
        });

        String expectedMessage = "No value present";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateBooking() {
        booking.setStatus(Booking.BookingStatus.REJECTED);

        bookingRepository.save(booking);

        Booking retrivedBooking = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(Booking.BookingStatus.REJECTED, retrivedBooking.getStatus());
    }

    @Test
    void testDeleteBooking() {
        bookingRepository.deleteById(booking.getId());

        Exception exception = assertThrows(Exception.class, () -> {
            bookingRepository.findById(booking.getId()).orElseThrow();
        });

        String expectedMessage = "No value present";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testWithoutRequiredFields() {
        Booking invalidBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        assertThrows(Exception.class, () -> bookingRepository.save(invalidBooking));
    }

    @Test
    void testAssociationWithItemAndUser() {
        assertNotNull(booking.getItem());
        assertEquals(item, booking.getItem());
        assertNotNull(booking.getBooker());
        assertEquals(user, booking.getBooker());
    }

    @Test
    void testDeleteItemWithAssociatedBookings() {
        bookingRepository.deleteById(booking.getId());
        itemRepository.deleteById(item.getId());

        assertFalse(itemRepository.existsById(item.getId()));
        assertFalse(bookingRepository.existsById(booking.getId()));
    }


    @Test
    void testOverlappingBookings() {
        Booking overlappingBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(3))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(Booking.BookingStatus.WAITING)
                .build();

        bookingRepository.save(overlappingBooking);

        var bookings = bookingRepository.findAll();
        assertEquals(2, bookings.size());
    }


}


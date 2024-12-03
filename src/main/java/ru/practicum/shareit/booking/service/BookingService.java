package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {

    BookingDtoOut addNewBooking(Long userId, BookingDtoIn bookingDtoIn);

    BookingDtoOut approveBooking(Long userId, Long bookingId, boolean approved);

    BookingDtoOut getBookingById(Long bookingId, Long userId);

    List<BookingDtoOut> getAllUserBooking(Long userId, State state);

    List<BookingDtoOut> getAllOwnerBooking(Long ownerId, State state);

}

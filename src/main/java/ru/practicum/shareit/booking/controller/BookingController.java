package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utility.Create;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @RequestBody @Validated(Create.class) BookingDtoIn bookingDtoIn) {
        log.info("запрос на добавление нового бронирования для пользователя {}: {}", userId, bookingDtoIn);
        return bookingService.addNewBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId,
                                        @RequestParam Boolean approved) {
        log.info("запрос на подтверждение бронированя от пользователя с ID: {}", userId);
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на получение бронирования с ID: {} для пользователя с ID: {}", bookingId, userId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllUserBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state) {
        State stateEnum = State.fromString(state.toUpperCase());
        return bookingService.getAllUserBooking(userId, stateEnum);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        log.info("запрос на Получение списка бронирований для всех вещей текущего пользователя с ID: {} ", ownerId);
        State steteEnum = State.fromString(state);
        return bookingService.getAllOwnerBooking(ownerId, steteEnum);
    }

}

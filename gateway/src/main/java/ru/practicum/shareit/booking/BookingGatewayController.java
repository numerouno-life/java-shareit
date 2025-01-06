package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.State;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingGatewayController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody BookItemRequestDto bookingDtoIn) {
        log.info("запрос на добавление нового бронирования для пользователя {}: {}", userId, bookingDtoIn);
        return bookingClient.addNewBooking(userId, bookingDtoIn);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        log.info("запрос на подтверждение бронированя от пользователя с ID: {}", userId);
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на получение бронирования с ID: {} для пользователя с ID: {}", bookingId, userId);
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        State stateEnum = State.fromString(state.toUpperCase());
        log.info("Получение всех бронирований для пользователя с ID: {} со статусом: {}", userId, stateEnum);
        return bookingClient.getAllUserBooking(userId, stateEnum);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwnerBooking(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                     @RequestParam(defaultValue = "ALL") String state) {
        State stateEnum = State.fromString(state.toUpperCase());
        log.info("Получение всех бронирований владельца с ID: {} со статусом: {}", ownerId, stateEnum);
        return bookingClient.getAllOwnerBooking(ownerId, stateEnum);
    }

}

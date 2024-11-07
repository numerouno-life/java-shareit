package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {

    @NotNull
    private Long id;

    @NotNull
    private LocalDateTime start;

    @NotNull
    private LocalDateTime end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    private BookingStatus status;

    public enum BookingStatus {
        WAITING, APPROVED, REJECTED, CANCELED
    }
}

package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utility.Create;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingDtoIn {
    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime start;

    @Future(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private LocalDateTime end;

    @NotNull(groups = {Create.class})
    private Long itemId;
}
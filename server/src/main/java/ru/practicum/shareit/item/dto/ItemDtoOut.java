package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoOut {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private List<CommentDtoOut> comments;
    private UserDtoShort owner;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private Long requestId;
}

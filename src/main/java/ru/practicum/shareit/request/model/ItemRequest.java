package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {

    @NotNull
    private Long id;

    @NotNull
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    private LocalDateTime created;
}

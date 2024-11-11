package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Getter
@Setter
@Builder
public class ItemRequest {

    @NotNull
    private Integer id;

    @NotNull
    private String description;

    @NotNull
    private User requestor;

    @NotNull
    private LocalDateTime created;
}

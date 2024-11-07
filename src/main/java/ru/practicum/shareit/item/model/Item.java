package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    private String description;

    @NotNull
    private Boolean available;

    @NotNull
    private User owner;

    private ItemRequest request;
}

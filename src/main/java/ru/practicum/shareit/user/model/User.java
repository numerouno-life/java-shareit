package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @Email(message = "Некорректный формат email")
    @NotNull
    private String email;
}

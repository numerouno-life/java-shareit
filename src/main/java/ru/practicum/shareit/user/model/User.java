package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@Builder
public class User {

    @NotNull
    private Long id;

    @NotNull
    private String name;

    @Email(message = "Некорректный формат email")
    @NotNull
    private String email;
}

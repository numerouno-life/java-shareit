package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utility.Create;
import ru.practicum.shareit.utility.Update;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoRequest {

    private Long id;

    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 255, groups = {Create.class, Update.class})
    private String name;

    @NotNull(message = "email должен быть задан")
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

}

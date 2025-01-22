package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.utility.Create;
import ru.practicum.shareit.utility.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemRequestDtoRequest {

    private long id;

    @NotBlank(groups = {Create.class, Update.class})
    @Size(max = 555, groups = {Create.class, Update.class})
    private String description;
}
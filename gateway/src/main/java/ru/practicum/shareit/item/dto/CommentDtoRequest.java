package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
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
public class CommentDtoRequest {
    private Long id;

    @Size(max = 1000, groups = {Create.class, Update.class})
    @NotBlank(groups = {Create.class})
    private String text;
}

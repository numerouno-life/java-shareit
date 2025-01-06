package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoIn {
    private String description;
}

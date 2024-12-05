package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoShort {
    private Long id;
    private String name;
}

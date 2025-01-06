package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDtoOut {
    private Long id;
    private String description;
    private LocalDateTime created;
    private Long requestorId;

    @Builder.Default
    private List<ItemDtoOut> items = new ArrayList<>();

}

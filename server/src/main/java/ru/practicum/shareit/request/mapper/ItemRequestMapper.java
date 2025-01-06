package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface ItemRequestMapper {

    ItemRequestDtoOut toRequestDtoOut(ItemRequest request);


    ItemRequest toRequest(ItemRequestDtoIn requestDtoIn);

    ItemRequest mapItemRequsetDtoToItem(ItemRequestDtoIn itemRequestDtoIn, User user);
}

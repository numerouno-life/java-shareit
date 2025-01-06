package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "requestId", expression = "java(item.getRequest() != null ? item.getRequest().getId() : null)")
    ItemDtoOut toItemDtoOut(Item item);

    ItemDto toItemDto(Item item);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "available", source = "available")
    Item toItem(ItemDtoIn itemDtoIn);

    @Mapping(target = "id", ignore = true) // Игнорируем поле id, если оно не должно заполняться
    @Mapping(target = "name", source = "itemDtoIn.name")
    @Mapping(target = "description", source = "itemDtoIn.description")
    @Mapping(target = "available", source = "itemDtoIn.available")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "request", source = "itemRequest")
    Item mapItemDtoToItem(ItemDtoIn itemDtoIn, User user, ItemRequest itemRequest);
}

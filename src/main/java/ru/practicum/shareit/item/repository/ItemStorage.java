package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemStorage {

    ItemDto addNewItem(Integer userId, ItemDto itemDto);

    ItemDto getItemById(Integer itemId);

    ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto);

    List<ItemDto> getOwnerItems(Integer userId);

    List<ItemDto> searchItemByText(String text);

}

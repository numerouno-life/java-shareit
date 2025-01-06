package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {

    ItemDtoOut addNewItem(Long userId, ItemDtoIn itemDtoIn);

    ItemDtoOut getItemById(Long itemId, Long userId);

    ItemDtoOut updateItem(Long userId, Long itemId, ItemDtoIn itemDtoIn);

    List<ItemDtoOut> getOwnerItems(Long userId);

    List<ItemDtoOut> searchItemByText(String text);

    CommentDtoOut saveComment(Long userId, CommentDtoIn commentDtoIn, Long itemId);
}

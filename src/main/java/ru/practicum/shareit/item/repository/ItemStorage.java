package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addNewItem(Item item);

    Item getItemById(Integer itemId);

    Item updateItem(Item updateItem);

    List<Item> getOwnerItems(Integer userId);

    List<Item> searchItemByText(String text);

}

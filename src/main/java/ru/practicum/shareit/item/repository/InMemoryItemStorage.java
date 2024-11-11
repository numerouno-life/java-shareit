package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {

    @Override
    public ItemDto addNewItem(Integer userId, ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        return null;
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        return null;
    }

    @Override
    public List<ItemDto> getOwnerItems(Integer userId) {
        return List.of();
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        return List.of();
    }
}

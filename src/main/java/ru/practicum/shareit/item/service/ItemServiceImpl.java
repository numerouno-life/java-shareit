package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

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

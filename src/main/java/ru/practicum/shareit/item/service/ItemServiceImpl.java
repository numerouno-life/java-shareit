package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto addNewItem(Integer userId, ItemDto itemDto) {
        log.info("Добавление новой вещи для пользователя {}: {}", userId, itemDto);
        userService.getUserById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemStorage.addNewItem(item));
    }

    @Override
    public ItemDto getItemById(Integer itemId) {
        log.info("Получение вещи с id {} для пользователя", itemId);
        return ItemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public ItemDto updateItem(Integer userId, Integer itemId, ItemDto itemDto) {
        log.info("Изменение вещи с id {} для пользователя {}: {}", itemId, userId, itemDto);
        userService.getUserById(userId);
        getItemById(itemId);
        getItemById(itemId);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        item.setOwner(userId);
        return ItemMapper.toItemDto(itemStorage.updateItem(item));
    }

    @Override
    public List<ItemDto> getOwnerItems(Integer userId) {
        log.info("Получение всех вещей для пользователя {}", userId);
        return ItemMapper.toItemDtoList(itemStorage.getOwnerItems(userId));
    }

    @Override
    public List<ItemDto> searchItemByText(String text) {
        log.info("Поиск вещей по тексту {}", text);
        return ItemMapper.toItemDtoList(itemStorage.searchItemByText(text));
    }
}

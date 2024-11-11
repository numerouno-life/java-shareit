package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private Integer currentId = 1;

    @Override
    public Item addNewItem(Item item) {
        log.info("Добавление новой вещи {}", item);
        item.setId(currentId++);
        items.put(item.getId(), item);
        log.info("Добавили новую вещь {}", item);
        return item;
    }

    @Override
    public Item getItemById(Integer itemId) {
        log.info("Получение вещи с id {}", itemId);
        if (!items.containsKey(itemId)) {
            log.error("Вещи с ID:{} не найдены", itemId);
            throw new NotFoundException("Вещи с ID:" + itemId + " не найдены");
        }
        return items.get(itemId);
    }

    @Override
    public Item updateItem(Item updateItem) {
        log.info("Обновление вещи {}", updateItem);
        Item item = items.get(updateItem.getId());
        Optional<Item> optionalItemUpdate = Optional.of(updateItem);
        optionalItemUpdate.map(Item::getName).ifPresent(item::setName);
        optionalItemUpdate.map(Item::getAvailable).ifPresent(item::setAvailable);
        optionalItemUpdate.map(Item::getDescription).ifPresent(item::setDescription);
        optionalItemUpdate.map(Item::getRequest).ifPresent(item::setRequest); // TODO <- не уверен в этом
        items.put(item.getId(), item);
        log.info("Обновили вещь {}", updateItem);
        return item;
    }

    @Override
    public List<Item> getOwnerItems(Integer userId) {
        log.info("Получение вещей пользователя {}", userId);
        return items.values().stream().filter(item -> item.getOwner().getId().equals(userId)).toList();
    }

    @Override
    public List<Item> searchItemByText(String text) {
        log.info("Поиск вещей по тексту {}", text);
        if (text == null || text.isEmpty()) {
            log.info("Текст пустой или отсутствует");
            return new ArrayList<>();
        }
        String textToLowerCase = text.toLowerCase();
        return items.values().stream().filter(item -> item.getAvailable() &&
                (item.getDescription().contains(textToLowerCase)) ||
                item.getName().contains(textToLowerCase)).toList();
    }
}

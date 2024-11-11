package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Later-User-Id") Integer userId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("запрос на добавление новой вещи для пользователя {}: {}", userId, itemDto);
        return itemService.addNewItem(userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Integer itemId) {
        log.info("запрос на получение вещи с id {} для пользователя", itemId);
        return itemService.getItemById(itemId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Later-User-Id") Integer userId,
                              @PathVariable Integer itemId,
                              @Valid @RequestBody ItemDto itemDto) {
        log.info("запрос на изменение вещи с id {} для пользователя {}: {}", itemId, userId, itemDto);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader("X-Later-User-Id") Integer userId) {
        log.info("запрос на получение всех вещей для пользователя {}", userId);
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    List<ItemDto> searchItemByText(@RequestParam String text) {
        log.info("запрос на поиск вещей по тексту: {}", text);
        return itemService.searchItemByText(text);
    }
}

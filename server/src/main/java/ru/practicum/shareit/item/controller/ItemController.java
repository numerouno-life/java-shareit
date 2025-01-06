package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utility.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDtoOut addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemDtoIn itemDtoIn) {
        log.info("запрос на добавление новой вещи для пользователя {}: {}", userId, itemDtoIn);
        return itemService.addNewItem(userId, itemDtoIn);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut getItemById(@PathVariable Long itemId,
                                  @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на получение вещи с id {} для пользователя", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody ItemDtoIn itemDtoIn) {
        log.info("запрос на изменение вещи с id {} для пользователя {}: {}", itemId, userId, itemDtoIn);
        return itemService.updateItem(userId, itemId, itemDtoIn);
    }

    @GetMapping
    public List<ItemDtoOut> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на получение всех вещей для пользователя {}", userId);
        return itemService.getOwnerItems(userId);
    }

    @GetMapping("/search")
    List<ItemDtoOut> searchItemByText(@RequestParam String text) {
        log.info("запрос на поиск вещей по тексту: {}", text);
        return itemService.searchItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    CommentDtoOut addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable Long itemId,
                             @Validated(Create.class) @RequestBody CommentDtoIn commentDtoIn) {
        log.info("Запрос на добавление комментария");
        return itemService.saveComment(itemId, commentDtoIn, userId);
    }
}

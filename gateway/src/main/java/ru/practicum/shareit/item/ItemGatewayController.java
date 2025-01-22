package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.utility.Create;


@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemGatewayController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated({Create.class}) @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("запрос на добавление новой вещи для пользователя {}: {}", userId, itemDtoRequest);
        return itemClient.addNewItem(userId, itemDtoRequest);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на получение вещи с id {} для пользователя с id {}", itemId, userId);
        return itemClient.getItemById(itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDtoRequest itemDtoRequest) {
        log.info("запрос на изменение вещи с id {} для пользователя {}: {}", itemId, userId, itemDtoRequest);
        return itemClient.updateItem(userId, itemId, itemDtoRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("запрос на получение всех вещей для пользователя {}", userId);
        return itemClient.getOwnerItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemByText(@RequestParam String text) {
        log.info("запрос на поиск вещей по тексту: {}", text);
        return itemClient.searchItemByText(text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Validated(Create.class) @RequestBody CommentDtoRequest commentDtoRequest) {
        log.info("Запрос на добавление комментария");
        return itemClient.saveComment(itemId, commentDtoRequest, userId);
    }
}

package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.utility.Create;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestGatewayController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Validated(Create.class) @RequestBody ItemRequestDtoRequest itemRequestDtoRequest) {
        log.info("запрос на добавление нового заявки для пользователя {}: {}", userId, itemRequestDtoRequest);
        return itemRequestClient.addNewRequest(userId, itemRequestDtoRequest);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerRequests(@RequestHeader("X-Sharer-User-id") Long userId) {
        log.info("Запрос на получение всех заявок для пользователя {}", userId);
        return itemRequestClient.getOwnerRequests(userId);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        log.info("запрос на получение всех заявок");
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestByIdWithItems(@RequestHeader("X-Sharer-User-id") Long userId,
                                                          @PathVariable Long requestId) {
        log.info("запрос на получение заявки с id {} для пользователя", requestId);
        return itemRequestClient.getRequestByIdWithItems(userId, requestId);
    }
}

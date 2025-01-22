package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDtoOut addNewRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemRequestDtoIn itemRequestDtoIn) {
        log.info("запрос на добавление нового заявки для пользователя {}: {}", userId, itemRequestDtoIn);
        return requestService.addNewRequest(userId, itemRequestDtoIn);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getOwnerRequests(@RequestHeader(
            value = "X-Sharer-User-id", required = true) Long userId) {
        log.info("получить список своих запросов по ID:{} вместе с данными об ответах на них", userId);
        return requestService.getOwnerRequests(userId);
    }


    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("получить список запросов, созданных другими пользователями");
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getRequestByIdWithItems(@RequestHeader("X-Sharer-User-id") Long userId,
                                                     @PathVariable Long requestId) {
        log.info("получить данные об одном конкретном запросе с ID:{} вместе с данными об ответах на него", requestId);
        return requestService.getRequestByIdWithItems(userId, requestId);
    }
}

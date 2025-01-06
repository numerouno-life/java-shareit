package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDtoOut addNewRequest(Long userId, ItemRequestDtoIn itemRequestDtoIn);

    List<ItemRequestDtoOut> getOwnerRequests(Long userId);

    List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestDtoOut getRequestByIdWithItems(Long userId, Long requestId);
}

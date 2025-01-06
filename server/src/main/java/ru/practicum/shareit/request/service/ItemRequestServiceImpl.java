package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Transactional
    @Override
    public ItemRequestDtoOut addNewRequest(Long userId, ItemRequestDtoIn itemRequestDtoIn) {
        log.info("Добавление новой заявки для пользователя {}", userId);
        User user = findUserById(userId);
        ItemRequest request = itemRequestMapper.toRequest(itemRequestDtoIn);
        request.setCreated(LocalDateTime.now());
        request.setRequestor(user);
        return itemRequestMapper.toRequestDtoOut(itemRequestRepository.save(request));
    }

    @Override
    public List<ItemRequestDtoOut> getOwnerRequests(Long userId) {
        log.info("получить список своих запросов с ID:{}", userId);
        findUserById(userId);
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);
        return addItems(requests);
    }

    @Override
    public List<ItemRequestDtoOut> getAllRequests(Long userId, Integer from, Integer size) {
        log.info("получить список запросов, созданных другими пользователями");
        findUserById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        List<ItemRequest> requests = itemRequestRepository.findAllByRequestorIdIsNot(userId, pageable);
        return addItems(requests);
    }

    @Override
    public ItemRequestDtoOut getRequestByIdWithItems(Long userId, Long requestId) {
        log.info("получить запрос с ID:{}", requestId);
        findUserById(userId);
        ItemRequest request = itemRequestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Запрос с ID " + requestId + " не найден"));
        ItemRequestDtoOut requestDtoOut = itemRequestMapper.toRequestDtoOut(request);
        requestDtoOut.setItems((itemRepository.findAllByOwnerId(userId).stream()
                .map(itemMapper::toItemDtoOut)
                .toList()));
        return requestDtoOut;
    }


    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private List<ItemRequestDtoOut> addItems(List<ItemRequest> requests) {
        log.debug("Добавление вещей {}", requests);
        return requests.stream()
                .map(request -> {
                    ItemRequestDtoOut requestDtoOut = itemRequestMapper.toRequestDtoOut(request);
                    List<ItemDtoOut> items = itemRepository.findAllByRequestId(request.getId()).stream()
                            .map(itemMapper::toItemDtoOut)
                            .toList();
                    requestDtoOut.setItems(items);
                    return requestDtoOut;
                })
                .toList();
    }
}
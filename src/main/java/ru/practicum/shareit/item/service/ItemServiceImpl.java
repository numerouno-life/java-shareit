package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private static final LocalDateTime NOW = LocalDateTime.now();
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ItemDtoOut addNewItem(Long userId, ItemDtoIn itemDtoIn) {
        log.info("Добавление новой вещи {}, пользователем {}", itemDtoIn, userId);
        if (itemDtoIn.getName() == null || itemDtoIn.getName().isEmpty()) {
            throw new ValidationException("Имя вещи не может быть пустым");
        }
        if (itemDtoIn.getDescription() == null || itemDtoIn.getDescription().isEmpty()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        User user = findUserById(userId);
        return ItemMapper.toItemDtoOut(itemRepository.save(ItemMapper.mapItemDtoToItem(itemDtoIn, user)));
    }

    @Override
    public ItemDtoOut getItemById(Long itemId, Long userId) {
        log.info("Получение вещи с ID: {} пользователя {}", itemId, userId);
        return itemRepository.findById(itemId).map(item ->
                enhanceItemWithDetails(item, userId)).orElseThrow(() ->
                new NotFoundException("Вещь с Id " + itemId + " не найдена"));
    }

    @Override
    public ItemDtoOut updateItem(Long userId, Long itemId, ItemDtoIn itemDtoIn) {
        log.info("Обновление вещи {}, c ID: {} пользователя {}", itemDtoIn.getName(), itemId, userId);
        findUserById(userId);
        Item item = findItemById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ValidationException("У владельца нет вещи");
        }
        Optional.ofNullable(itemDtoIn.getName()).ifPresent(item::setName);
        Optional.ofNullable(itemDtoIn.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(itemDtoIn.getAvailable()).ifPresent(item::setAvailable);
        Item saveItem = itemRepository.save(item);
        log.info("Вещь обновлена {}", saveItem.getName());
        return ItemMapper.toItemDtoOut(saveItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDtoOut> getOwnerItems(Long userId) {
        log.info("Получение списка всех вещей владельца с ID:{}", userId);
        findUserById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);

        if (items.isEmpty()) {
            log.info("Владельцу с ID: {} нет вещей", userId);
            return Collections.emptyList();
        }

        return items.stream()
                .map(item -> {
                    ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
                    itemDtoOut.setLastBooking(getLastBooking(item));
                    itemDtoOut.setNextBooking(getNextBooking(item));
                    itemDtoOut.setComments(getComments(item));
                    return itemDtoOut;
                })
                .toList();
    }


    @Override
    public List<ItemDtoOut> searchItemByText(String text) {
        log.info("Поиск вещей по тексту {}", text);
        if (text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDtoOut)
                .toList();
    }

    @Override
    public CommentDtoOut saveComment(Long itemId, CommentDtoIn commentDtoIn, Long userId) {
        log.info("Сохранение комментария к вещи с ID: {} пользователя {}", itemId, userId);
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Вещь с ID: " + itemId + " не найдена");
        }
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с ID: " + userId + " не найден");
        }
        try {
            Booking booking = bookingRepository
                    .findByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId,
                            Booking.BookingStatus.APPROVED, LocalDateTime.now());
            return CommentMapper.toCommentDtoOut(commentRepository.save(
                    CommentMapper.toComment(commentDtoIn, booking.getItem(), booking.getBooker())));
        } catch (Exception e) {
            throw new ValidationException("Бронь для вещи с ID: " + itemId + " не найдена");
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID " + userId + " не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID " + itemId + " не найдена"));
    }

    private ItemDtoOut enhanceItemWithDetails(Item item, Long userId) {
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
        if (itemDtoOut.getOwner().getId().equals(userId)) {
            itemDtoOut.setLastBooking(getLastBooking(item));
            itemDtoOut.setNextBooking(getLastBooking(item));
        }
        itemDtoOut.setComments(getComments(item));
        return itemDtoOut;
    }

    private BookingDto getLastBooking(Item item) {
        return bookingRepository
                .findFirstByItemIdAndStartLessThanEqualAndStatus(item.getId(), NOW,
                        Booking.BookingStatus.APPROVED, Sort.by(DESC, "end"))
                .map(BookingMapper::toBookingDto)
                .orElse(null);
    }

    private BookingDto getNextBooking(Item item) {
        return bookingRepository
                .findFirstByItemIdAndStartAfterAndStatus(item.getId(), NOW,
                        Booking.BookingStatus.APPROVED, Sort.by(ASC, "end"))
                .map(BookingMapper::toBookingDto)
                .orElse(null);
    }

    private List<CommentDtoOut> getComments(Item item) {
        return commentRepository.findAllByItemId(item.getId()).stream()
                .map(CommentMapper::toCommentDtoOut)
                .toList();
    }
}

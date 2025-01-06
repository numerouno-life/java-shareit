package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AuthorizationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingDtoOut addNewBooking(Long userId, BookingDtoIn bookingDtoIn) {
        log.info("Добавление нового бронирования");
        if (bookingDtoIn.getStart() == bookingDtoIn.getEnd()) {
            throw new ValidationException("Дата начала и конца бронирования должны быть разными");
        }
        User user = findUserById(userId);
        Item item = findItemById(bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new ValidationException("Вещь не доступна для бронированя");
        }

        return BookingMapper.toBookingDtoOut(bookingRepository.save(
                BookingMapper.toBooking(bookingDtoIn, user, item, Booking.BookingStatus.WAITING)));
    }

    @Override
    @Transactional
    public BookingDtoOut approveBooking(Long userId, Long bookingId, boolean approved) {
        log.info("Одобрение бронирования");
        Booking booking = findBookingById(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new AuthorizationException("Изменять бронирования может только собственник вещи");
        }
        if (!booking.getStatus().equals(Booking.BookingStatus.WAITING)) {
            throw new ValidationException("Бронирование уже обработано. Текущий статус: " + booking.getStatus());
        }
        booking.setStatus(approved ? Booking.BookingStatus.APPROVED : Booking.BookingStatus.REJECTED);
        booking.getItem().setAvailable(false);
        bookingRepository.save(booking);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut getBookingById(Long bookingId, Long userId) {
        log.info("Получение бронирования по Id {}", bookingId);
        findUserById(userId);
        Booking booking = findBookingById(bookingId);
        if (!(booking.getItem().getOwner().getId().equals(userId)) && (!booking.getBooker().getId().equals(userId))) {
            throw new AuthorizationException(
                    "Получить информацию о бронировании могут только владелец вещи или автор бронирования");
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getAllUserBooking(Long userId, State state) {
        log.info("Получение списка всех бронирований пользователя с Id {}", userId);
        findUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case CURRENT -> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now);
            case PAST -> bookings = bookingRepository.findByBookerIdAndEndBefore(userId, now);
            case FUTURE -> bookings = bookingRepository.findByBookerIdAndStartAfter(userId, now);
            case WAITING -> bookings = bookingRepository.findByBookerIdAndStatus(userId, Booking.BookingStatus.WAITING);
            case REJECTED ->
                    bookings = bookingRepository.findByBookerIdAndStatus(userId, Booking.BookingStatus.REJECTED);
            case ALL -> bookings = bookingRepository.findByBookerId(userId);
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }
        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return bookings.stream()
                .map(BookingMapper::toBookingDtoOut)
                .toList();
    }

    @Override
    public List<BookingDtoOut> getAllOwnerBooking(Long ownerId, State state) {
        log.info("Получение списка бронирований для всех вещей текущего пользователя с Id {}", ownerId);
        findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings;

        switch (state) {
            case CURRENT -> bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now);
            case PAST -> bookings = bookingRepository.findByItemOwnerIdAndEndBefore(ownerId, now);
            case FUTURE -> bookings = bookingRepository.findByItemOwnerIdAndStartAfter(ownerId, now);
            case WAITING ->
                    bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, Booking.BookingStatus.WAITING);
            case REJECTED ->
                    bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, Booking.BookingStatus.REJECTED);
            case ALL -> bookings = bookingRepository.findByItemOwnerId(ownerId);
            default -> throw new IllegalArgumentException("Неизвестное состояние: " + state);
        }
        bookings.sort(Comparator.comparing(Booking::getStart).reversed());
        return bookings.stream()
                .map(BookingMapper::toBookingDtoOut)
                .toList();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь с ID:" + userId + " не найден"));
    }

    private Item findItemById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new NotFoundException("Вещь с Id " + itemId + " не найдена"));
    }

    private Booking findBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Бронирование с Id " + bookingId + " не найдено"));
    }

}

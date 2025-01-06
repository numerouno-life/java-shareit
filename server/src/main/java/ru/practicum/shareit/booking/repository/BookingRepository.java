package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // nextBooking
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatus(Long itemId, LocalDateTime localDateTime,
                                                              Booking.BookingStatus status, Sort end);

    // lastBooking
    Optional<Booking> findFirstByItemIdAndStartLessThanEqualAndStatus(Long itemId, LocalDateTime localDateTime,
                                                                      Booking.BookingStatus status, Sort end);

    List<Booking> findByBookerId(Long userId);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long userId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndEndBefore(Long userId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartAfter(Long userId, LocalDateTime start);

    List<Booking> findByBookerIdAndStatus(Long userId, Booking.BookingStatus status);

    List<Booking> findByItemOwnerId(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime start);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, Booking.BookingStatus status);

    Booking findByBookerIdAndItemIdAndStatusAndEndBefore(
            Long bookerId, Long itemId, Booking.BookingStatus status, LocalDateTime time);

}

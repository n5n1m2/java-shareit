package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findAllByBooker_Id(int userId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(int bookerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndEndIsBefore(int bookerId, LocalDateTime end);

    List<Booking> findAllByBooker_IdAndStartIsBefore(int bookerId, LocalDateTime start);

    List<Booking> findAllByBooker_IdAndStatus(int bookerId, BookingStatus status);

    List<Booking> findAllByItemOwnerId(int itemOwnerId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(int itemOwnerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(int itemOwnerId, LocalDateTime end);

    List<Booking> findAllByItemOwnerIdAndStartIsBefore(int itemOwnerId, LocalDateTime start);

    List<Booking> findAllByItemOwnerIdAndStatus(int itemOwnerId, BookingStatus status);

    List<Booking> findAllByItemIdInAndEndBeforeOrderByEndDesc(List<Integer> ids, LocalDateTime end);

    List<Booking> findAllByItemIdInAndStartAfterOrderByStartAsc(List<Integer> ids, LocalDateTime end);

    List<Booking> findAllByItemIdAndBookerIdAndEndBeforeOrderByEndDesc(Integer itemId, Integer bookerId, LocalDateTime end);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Integer itemId, LocalDateTime end);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Integer itemId, LocalDateTime start);


}

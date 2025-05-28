package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.State;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.mapper.BookingDtoMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.exceptions.NoHavePermissionException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public BookingDtoOutput createBooking(BookingDtoInput dtoInput, int userId) {
        Item item = itemRepository.findById(dtoInput.getItemId()).orElseThrow(() ->
                new NotFoundException("item with id " + dtoInput.getItemId() + " not found"));
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("user with id " + userId + " not found"));

        validateBooking(dtoInput, item);

        Booking booking = new Booking(
                null,
                dtoInput.getStart(),
                dtoInput.getEnd(),
                item,
                user,
                dtoInput.getStatus()
        );

        booking.setBooker(userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found")));

        return BookingDtoMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDtoOutput updateBooking(int bookingId, int userId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("booking with id " + bookingId + " not found"));

        validatePermission(booking, userId);

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingDtoMapper.toDto(bookingRepository.save(booking));
    }

    public BookingDtoOutput getBookingById(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new NotFoundException("Booking with id " + bookingId + " not found"));

        if (userId == booking.getBooker().getId() || userId == booking.getItem().getOwner().getId()) {
            return BookingDtoMapper.toDto(booking);
        } else {
            throw new NoHavePermissionException("User with id " + userId + " not authorized to view booking");
        }
    }

    public List<BookingDtoOutput> getBookings(int userId, State state) {
        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.findAllByBooker_Id(userId);
            case WAITING -> bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.WAITING);
            case PAST -> bookings =
                    bookingRepository.findAllByBooker_IdAndEndIsBefore(userId, LocalDateTime.now().minusMinutes(1));
            case REJECTED -> bookings = bookingRepository.findAllByBooker_IdAndStatus(userId, BookingStatus.REJECTED);
            case FUTURE -> bookings = bookingRepository.findAllByBooker_IdAndStartIsBefore(userId, LocalDateTime.now());
            case CURRENT -> bookings = bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfter(userId,
                    LocalDateTime.now(),
                    LocalDateTime.now());
            default -> throw new IllegalArgumentException("Invalid state");
        }
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart))
                .map(BookingDtoMapper::toDto)
                .toList();
    }

    public List<BookingDtoOutput> getBookingsForOwner(int itemOwnerId, State state) {
        List<Booking> bookings;
        switch (state) {
            case ALL -> bookings = bookingRepository.findAllByItemOwnerId(itemOwnerId);
            case WAITING ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(itemOwnerId, BookingStatus.WAITING);
            case PAST -> bookings =
                    bookingRepository.findAllByItemOwnerIdAndEndIsBefore(itemOwnerId, LocalDateTime.now().minusMinutes(1));
            case REJECTED ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStatus(itemOwnerId, BookingStatus.REJECTED);
            case FUTURE ->
                    bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBefore(itemOwnerId, LocalDateTime.now());
            case CURRENT -> bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(itemOwnerId,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            default -> throw new IllegalArgumentException("Invalid state");
        }
        if (bookings.isEmpty()) {
            throw new NotFoundException("No bookings found for owner " + itemOwnerId);
        }
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart))
                .map(BookingDtoMapper::toDto)
                .toList();
    }

    private void validateBooking(BookingDtoInput dtoInput, Item item) {
        if (dtoInput == null) {
            throw new IllegalArgumentException("Booking is null");
        }
        if (dtoInput.getStart().isAfter(dtoInput.getEnd()) || dtoInput.getEnd().equals(dtoInput.getStart())) {
            throw new IllegalArgumentException("Start date is after end date");
        }
        if (!item.getAvailable()) {
            throw new IllegalArgumentException("Item is not available");
        }
        if (dtoInput.getStatus() == null) {
            dtoInput.setStatus(BookingStatus.WAITING);
        }
    }

    private void validatePermission(Booking booking, int userId) {
        if (userId != booking.getItem().getOwner().getId()) {
            throw new NoHavePermissionException("user with id " + userId + " not allowed to update booking");
        }
    }

}

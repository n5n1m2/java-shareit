package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public record BookingDtoWithoutItem(Integer id,
                                    LocalDateTime start,
                                    LocalDateTime end,
                                    User booker,
                                    BookingStatus status) {

}

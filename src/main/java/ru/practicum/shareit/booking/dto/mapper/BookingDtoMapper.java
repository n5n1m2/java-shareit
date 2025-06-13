package ru.practicum.shareit.booking.dto.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;

public class BookingDtoMapper {

    public static BookingDtoOutput toDto(Booking booking) {
        return new BookingDtoOutput(booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getItem(),
                booking.getBooker(),
                booking.getStatus());
    }

    public static Booking toBooking(BookingDtoOutput bookingDtoOutput) {
        return new Booking(bookingDtoOutput.getId(),
                bookingDtoOutput.getStart(),
                bookingDtoOutput.getEnd(),
                bookingDtoOutput.getItem(),
                bookingDtoOutput.getBooker(),
                bookingDtoOutput.getStatus());
    }

    public static BookingDtoWithoutItem toDtoWithoutItem(Booking booking) {
        if (booking == null) {
            return null;
        }
        return new BookingDtoWithoutItem(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getBooker(),
                booking.getStatus()
        );
    }
}

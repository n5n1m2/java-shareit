package ru.practicum.shareit.booking.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.in.BookingDtoInput;
import ru.practicum.shareit.booking.dto.out.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.out.BookingDtoWithoutItem;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Mapper(componentModel = "spring")
public interface BookingDtoMapper {

    BookingDtoOutput toBookingDtoOutput(Booking booking);

    @Mapping(target = "booker", source = "user")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "id", source = "bookingDtoInput.id")
    Booking toBooking(BookingDtoInput bookingDtoInput, Item item, User user);

    BookingDtoWithoutItem toBookingDtoWithoutItem(Booking booking);
}

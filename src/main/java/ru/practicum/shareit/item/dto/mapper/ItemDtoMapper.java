package ru.practicum.shareit.item.dto.mapper;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.mapper.BookingDtoMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemDtoMapper {
    public static ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable() != null ? item.getAvailable() : false,
                item.getOwner(),
                item.getRequest() != null ? item.getRequest() : null);
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getOwner(),
                itemDto.getRequest());
    }

    public static Item toItem(ItemDto itemDto, User user) {
        return new Item(itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemDto.getRequest());
    }

    public static ItemDtoWithBookingAndComments toDtoWithBooking(Item item,
                                                                        Booking lastBooking,
                                                                        Booking nextBooking,
                                                                        List<CommentDto> comments) {
        return new ItemDtoWithBookingAndComments(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner(),
                item.getRequest(),
                BookingDtoMapper.toDtoWithoutItem(lastBooking),
                BookingDtoMapper.toDtoWithoutItem(nextBooking),
                comments
        );
    }
}

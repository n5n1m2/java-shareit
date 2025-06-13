package ru.practicum.shareit.item.dto;

import lombok.Getter;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutItem;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Getter
public class ItemDtoWithBookingAndComments extends ItemDto {
    private BookingDtoWithoutItem lastBooking;
    private BookingDtoWithoutItem nextBooking;
    private List<CommentDto> comments;

    public ItemDtoWithBookingAndComments(Integer id, String name, String description, Boolean available, User owner, ItemRequest request, BookingDtoWithoutItem lastBooking, BookingDtoWithoutItem nextBooking, List<CommentDto> comments) {
        super(id, name, description, available, owner, request);
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
        this.comments = comments;
    }
}

package ru.practicum.shareit.item.dto.out;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.out.BookingDtoWithoutItem;
import ru.practicum.shareit.item.dto.in.CommentDto;
import ru.practicum.shareit.request.dto.our.ItemRequestDtoOutput;
import ru.practicum.shareit.user.User;

import java.util.List;

@Getter
@Setter
public class ItemDtoWithBookingAndComments extends ItemDtoOutput {
    private BookingDtoWithoutItem lastBooking;
    private BookingDtoWithoutItem nextBooking;
    private List<CommentDto> comments;

    public ItemDtoWithBookingAndComments(Integer id,
                                         String name,
                                         String description,
                                         Boolean available,
                                         User owner,
                                         ItemRequestDtoOutput request,
                                         List<CommentDto> comments,
                                         BookingDtoWithoutItem nextBooking,
                                         BookingDtoWithoutItem lastBooking) {
        super(id, name, description, available, owner, request);
        this.comments = comments;
        this.nextBooking = nextBooking;
        this.lastBooking = lastBooking;
    }
}

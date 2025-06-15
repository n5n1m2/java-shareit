package ru.practicum.shareit.item.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.mapper.BookingDtoMapper;
import ru.practicum.shareit.item.dto.in.CommentDto;
import ru.practicum.shareit.item.dto.in.ItemDto;
import ru.practicum.shareit.item.dto.out.ItemDtoOutput;
import ru.practicum.shareit.item.dto.out.ItemDtoShort;
import ru.practicum.shareit.item.dto.out.ItemDtoWithBookingAndComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookingDtoMapper.class})
public interface ItemMapper {

    @Mapping(target = "request", source = "itemRequest")
    @Mapping(target = "owner", source = "user")
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    @Mapping(target = "description", source = "itemDto.description")
    Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "authorName", source = "user.name")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDtoShort toItemDtoShort(Item item);

    ItemDtoOutput toItemDtoOutput(Item item);

    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "comments", source = "comments")
    @Mapping(target = "id", source = "item.id")
    ItemDtoWithBookingAndComments toItemDtoWithBookingAndComments(Item item,
                                                                  Booking lastBooking,
                                                                  Booking nextBooking,
                                                                  List<CommentDto> comments);
}

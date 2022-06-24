package ru.practicum.shareit.request.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.out.ItemDtoShort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.in.ItemRequestDto;
import ru.practicum.shareit.request.dto.our.ItemRequestDtoOutput;
import ru.practicum.shareit.user.User;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestDtoMapper {

    @Mapping(target = "items", source = "items")
    ItemRequestDtoOutput toDtoOutput(ItemRequest itemRequest, List<ItemDtoShort> items);

    ItemRequestDtoOutput toDtoOutput(ItemRequest itemRequest);

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "requester", source = "user")
    @Mapping(target = "created", expression = "java(dto.getCreated() != null ? dto.getCreated() : java.time.LocalDateTime.now())")
    ItemRequest toItemRequest(ItemRequestDto dto, User user);
}

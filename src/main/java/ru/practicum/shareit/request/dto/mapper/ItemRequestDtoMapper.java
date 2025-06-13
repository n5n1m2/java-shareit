package ru.practicum.shareit.request.dto.mapper;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public class ItemRequestDtoMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getRequestDate());
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                itemRequestDto.getRequester(),
                itemRequestDto.getRequestDate());
    }
}

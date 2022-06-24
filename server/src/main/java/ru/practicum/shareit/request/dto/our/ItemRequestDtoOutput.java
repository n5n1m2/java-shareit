package ru.practicum.shareit.request.dto.our;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.dto.out.ItemDtoShort;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestDtoOutput extends ItemRequest {
    private List<ItemDtoShort> items;

    public ItemRequestDtoOutput(Integer id, String description, User requester, LocalDateTime requestDate, List<ItemDtoShort> items) {
        super(id, description, requester, requestDate);
        this.items = items;
    }
}

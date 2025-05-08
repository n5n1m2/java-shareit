package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    public ItemDto addItem(Item item, Integer userId) {
        item.setOwner(userStorage.getUser(userId));
        return ItemDto.toItemDto(itemStorage.addItem(item));
    }

    public ItemDto updateItem(Item item, Integer userId) {
        item.setOwner(userStorage.getUser(userId));
        return ItemDto.toItemDto(itemStorage.updateItem(item));
    }

    public ItemDto getItemById(Integer id) {
        return ItemDto.toItemDto(itemStorage.getItem(id));
    }

    public List<ItemDto> getAllItems(int id) {
        return itemStorage.getItems(id).stream().map(ItemDto::toItemDto).collect(Collectors.toList());
    }
}

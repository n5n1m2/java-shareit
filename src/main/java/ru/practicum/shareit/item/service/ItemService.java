package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
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
        List<ItemDto> list = itemStorage.getItems(id)
                .stream()
                .map(ItemDto::toItemDto)
                .toList();
        if (list.isEmpty()) {
            throw new NotFoundException("Items not found for id " + id);
        }
        return list;
    }

    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return itemStorage.getAllItems()
                .stream()
                .filter(obj -> (
                        (obj.getName() != null && obj.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (obj.getDescription() != null && obj.getDescription().toLowerCase().contains(text.toLowerCase()))
                ) && obj.getAvailable())
                .map(ItemDto::toItemDto)
                .collect(Collectors.toList());
    }
}

package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
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

    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        // Проверка на существование пользователя проходит в методе userStorage.getUser()
        // Если пользователя с данным Id в хранилище нет, то выбрасывается NotFoundException
        itemValidation(itemDto, false);
        Item item = ItemDtoMapper.fromItemDto(itemDto, userStorage.getUser(userId));
        return itemStorage.addItem(item);
    }

    public ItemDto updateItem(ItemDto itemDto, Integer userId) {
        // Проверка на существование пользователя проходит в методе userStorage.getUser()
        // Если пользователя с данным Id в хранилище нет, то выбрасывается NotFoundException
        itemValidation(itemDto, true);
        Item item = ItemDtoMapper.fromItemDto(itemDto, userStorage.getUser(userId));
        return itemStorage.updateItem(item);
    }

    public ItemDto getItemById(Integer id) {
        return ItemDtoMapper.toItemDto(itemStorage.getItem(id));
    }

    public List<ItemDto> getAllItems(int id) {
        List<ItemDto> list = itemStorage.getUserItems(id)
                .stream()
                .map(ItemDtoMapper::toItemDto)
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
                .map(ItemDtoMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Integer getNextId() {
        return itemStorage.getAllItems()
                .stream()
                .map(Item::getId)
                .max(Integer::compareTo)
                .orElse(itemStorage.getAllItems().size() + 1) + 1;
    }

    private void itemValidation(ItemDto itemDto, boolean forUpdate) {
        if (!forUpdate && itemDto.getId() == null) {
            itemDto.setId(getNextId());
        } else if (forUpdate && itemDto.getId() == null) {
            throw new IllegalArgumentException("Item id is null");
        }
        // Из-за оценки по короткому замыканию проверяется только forUpdate для запроса из метода добавления,
        // поэтому затраты производительности минимальны.
        if (forUpdate && getItemById(itemDto.getId()) == null) {
            throw new NotFoundException("Item not found for id " + itemDto.getId());
        }
    }
}

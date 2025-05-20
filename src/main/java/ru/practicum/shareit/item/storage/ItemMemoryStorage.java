package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMemoryStorage implements ItemStorage {
    private HashMap<Integer, Item> items = new HashMap<>();


    @Override
    public ItemDto addItem(Item item) {
        items.put(item.getId(), item);
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public Item getItem(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Item not found for id " + id);
        }
    }

    @Override
    public List<Item> getUserItems(int ownerId) {
        return items.values().stream().filter(item -> item.getOwner().getId() == ownerId).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public ItemDto updateItem(Item item) {
        items.put(item.getId(), item);
        return ItemDtoMapper.toItemDto(item);
    }

    @Override
    public void deleteItem(int id) {
        items.remove(id);
    }
}

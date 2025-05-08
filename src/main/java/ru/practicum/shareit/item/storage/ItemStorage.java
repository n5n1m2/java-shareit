package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    ItemDto addItem(Item item);

    Item getItem(int id);

    List<Item> getUserItems(int id);

    List<Item> getAllItems();

    ItemDto updateItem(Item item);

    void deleteItem(int id);
}

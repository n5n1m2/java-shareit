package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(Item item);

    Item getItem(int id);

    List<Item> getItems(int id);

    List<Item> getAllItems();

    Item updateItem(Item item);

    void deleteItem(int id);
}

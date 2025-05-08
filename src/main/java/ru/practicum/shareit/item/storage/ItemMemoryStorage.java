package ru.practicum.shareit.item.storage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.NoHavePermissionException;
import ru.practicum.shareit.item.model.Item;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ItemMemoryStorage implements ItemStorage {
    private HashMap<Integer, Item> items = new HashMap<>();


    @Override
    public Item addItem(Item item) {
        if (item.getId() == null) {
            item.setId(getNextId());
        }
        items.put(item.getId(), item);
        return getItem(item.getId());
    }

    @Override
    public Item getItem(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> getItems(int id) {
        return items.values().stream().filter(item -> item.getOwner().getId() == id).collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Item item) {
        Item oldItem = items.get(item.getId());
        if (oldItem.getOwner().getId() != item.getOwner().getId()) {
            throw new NoHavePermissionException("Only owner can update this Item");
        }
        copyFields(oldItem, item);
        deleteItem(oldItem.getId());
        return addItem(item);
    }

    @Override
    public void deleteItem(int id) {
        items.remove(id);
    }

    private void copyFields(Item old, Item newItem) {
        BeanUtils.copyProperties(newItem, old, getNotNullFields(newItem));
    }

    private String[] getNotNullFields(Object object) {
        BeanWrapper wrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] propertyDescriptors = wrapper.getPropertyDescriptors();
        Set<String> emptyFields = new HashSet<>();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            Object value = wrapper.getPropertyValue(propertyDescriptor.getName());
            if (value == null) {
                emptyFields.add(propertyDescriptor.getName());
            }
        }
        return emptyFields.toArray(new String[0]);
    }

    private Integer getNextId() {
        return items.keySet()
                .stream()
                .max(Integer::compareTo)
                .orElse(items.size() + 1);
    }
}

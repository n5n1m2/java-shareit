package ru.practicum.shareit.user.storage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.ConflictException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;

import java.beans.PropertyDescriptor;
import java.util.*;

@Component
public class UserMemoryStorage implements UserStorage {
    private HashMap<Integer, User> users = new HashMap<>();

    @Override
    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("User with id " + id + " not found");
        }
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        userValidation(user);
        users.put(user.getId(), user);
        return getUser(user.getId());
    }

    @Override
    public void removeUser(int id) {
        if (!users.containsKey(id)) {
            return;
        }
        users.remove(id);
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());
        emailValidation(user);
        copyFields(oldUser, user);
        removeUser(user.getId());
        return addUser(oldUser);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void emailValidation(User user) {
        if (users.values().stream()
                .anyMatch(
                        user1 -> user1.getEmail().equalsIgnoreCase(user.getEmail())
                                && !Objects.equals(user1.getId(), user.getId())
                )) {
            throw new ConflictException("Email already exists " + user.toString());
        }
    }

    private Integer getNextId() {
        return users.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(users.size() + 1) + 1;
    }

    private User userValidation(User user) {
        if (user.getId() == null) {
            user.setId(getNextId());
        }
        emailValidation(user);
        if (users.containsKey(user.getId())) {
            throw new ConflictException("User with id " + user.getId() + " is already exits");
        }
        return user;
    }

    private void copyFields(User old, User newUser) {
        BeanUtils.copyProperties(newUser, old, getNotNullFields(newUser));
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
}

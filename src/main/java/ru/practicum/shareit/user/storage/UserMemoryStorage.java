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
    private Set<String> emails = new HashSet<>();

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
        emails.add(user.getEmail());
        return getUser(user.getId());
    }

    @Override
    public void removeUser(int id) {
        if (!users.containsKey(id)) {
            return;
        }
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());
        emailValidation(user.getEmail());
        copyFields(oldUser, user);
        removeUser(user.getId());
        return addUser(oldUser);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void emailValidation(String email) {
        if (email != null && emails.contains(email)) {
            System.out.println("\n\n\n\n" + email + "\n\n" + emails + "\n\n");
            throw new ConflictException("Email is already in use");
        }
    }

    private Integer getNextId() {
        return users.keySet()
                .stream()
                .max(Integer::compare)
                .orElse(users.size() + 1);
    }

    private User userValidation(User user) {
        emailValidation(user.getEmail());
        if (user.getId() == null) {
            user.setId(getNextId());
        }
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

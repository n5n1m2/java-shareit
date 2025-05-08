package ru.practicum.shareit.user.storage;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;

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
    public UserDto addUser(UserDto userDto) {
        users.put(userDto.getId(), UserDtoMapper.getUser(userDto));
        return userDto;
    }

    @Override
    public void removeUser(int id) {
        if (!users.containsKey(id)) {
            return;
        }
        users.remove(id);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto oldUser = UserDtoMapper.getUserDto(users.get(userDto.getId()));
        copyFields(oldUser, userDto);
        removeUser(userDto.getId());
        return addUser(oldUser);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    private void copyFields(UserDto old, UserDto newUser) {
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

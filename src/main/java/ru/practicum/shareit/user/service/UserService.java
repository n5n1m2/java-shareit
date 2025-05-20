package ru.practicum.shareit.user.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserService {
    private UserStorage userStorage;

    public UserDto getUser(int id) {
        return UserDtoMapper.getUserDto(userStorage.getUser(id));
    }

    public List<UserDto> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers()).stream().map(UserDtoMapper::getUserDto).collect(Collectors.toList());
    }

    public UserDto addUser(UserDto userDto) {
        userValidation(userDto, false);
        return userStorage.addUser(userDto);
    }

    public void removeUser(int id) {
        userStorage.removeUser(id);
    }

    public UserDto updateUser(UserDto userDto) {
        userValidation(userDto, true);
        UserDto oldUser = UserDtoMapper.getUserDto(userStorage.getUser(userDto.getId()));
        copyFields(oldUser, userDto);
        removeUser(userDto.getId());
        return userStorage.updateUser(oldUser);
    }

    private void userValidation(UserDto userDto, boolean forUpdate) {
        if (userDto.getId() == null) {
            userDto.setId(getNextId());
        }
        emailValidation(userDto);
        Optional<UserDto> user = getAllUsers()
                .stream()
                .filter(u -> Objects.equals(u.getId(), userDto.getId())).findFirst();
        if (forUpdate && user.isPresent()) {
            return;
        } else if (user.isPresent()) {
            throw new ConflictException("User with id " + userDto.getId() + " already exists");
        }
    }

    private void emailValidation(UserDto userDto) {
        if (getAllUsers().stream()
                .anyMatch(
                        user1 -> user1.getEmail().equalsIgnoreCase(userDto.getEmail())
                                && !Objects.equals(user1.getId(), userDto.getId())
                )) {
            throw new ConflictException("Email already exists " + userDto.toString());
        }
    }

    private Integer getNextId() {
        return getAllUsers()
                .stream()
                .map(UserDto::getId)
                .max(Integer::compare)
                .orElse(getAllUsers().size() + 1) + 1;
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

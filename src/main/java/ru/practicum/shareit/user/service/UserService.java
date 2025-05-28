package ru.practicum.shareit.user.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.ConflictException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;

    public UserDto getUser(int id) {
        return UserDtoMapper.toDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found")));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto addUser(UserDto userDto) {
        userValidation(userDto, false);
        return UserDtoMapper.toDto(userRepository.save(UserDtoMapper.toUser(userDto)));
    }

    public void removeUser(int id) {
        userRepository.deleteById(id);
    }

    public UserDto updateUser(UserDto userDto) {
        userValidation(userDto, true);
        UserDto oldUser = getUser(userDto.getId());
        copyFields(oldUser, userDto);
        UserDto userDto1 = UserDtoMapper.toDto(userRepository.save(UserDtoMapper.toUser(oldUser)));
        return userDto1;
    }

    private void userValidation(UserDto userDto, boolean forUpdate) {
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
            throw new ConflictException("Email already exists " + userDto);
        }
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

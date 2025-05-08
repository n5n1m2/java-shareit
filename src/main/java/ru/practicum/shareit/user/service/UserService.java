package ru.practicum.shareit.user.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.ConflictException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        return userStorage.updateUser(userDto);
    }

    private void userValidation(UserDto userDto, boolean forUpdate) {
        if (userDto.getId() == null) {
            userDto.setId(getNextId());
        }
        emailValidation(userDto);
        Optional<UserDto> user = getAllUsers()
                .stream()
                .filter(u -> Objects.equals(u.getId(), userDto.getId())).findFirst();
        // Из-за оценки по короткому замыканию проверяется только forUpdate для запроса из метода добавления,
        // поэтому затраты производительности минимальны.
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
}

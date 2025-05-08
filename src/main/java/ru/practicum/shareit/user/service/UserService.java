package ru.practicum.shareit.user.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserService {
    private UserStorage userStorage;

    public UserDto getUser(int id) {
        return UserDto.getUserDto(userStorage.getUser(id));
    }

    public List<UserDto> getAllUsers() {
        return new ArrayList<>(userStorage.getAllUsers()).stream().map(UserDto::getUserDto).collect(Collectors.toList());
    }

    public UserDto addUser(User user) {
        return UserDto.getUserDto(userStorage.addUser(user));
    }

    public void removeUser(int id) {
        userStorage.removeUser(id);
    }

    public UserDto updateUser(User user) {
        return UserDto.getUserDto(userStorage.updateUser(user));
    }
}

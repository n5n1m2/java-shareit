package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserStorage {
    User getUser(int id);

    UserDto addUser(UserDto userDto);

    void removeUser(int id);

    UserDto updateUser(UserDto userDto);

    List<User> getAllUsers();
}

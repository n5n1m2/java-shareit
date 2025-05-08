package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserStorage {
    User getUser(int id);

    User addUser(User user);

    void removeUser(int id);

    User updateUser(User user);

    List<User> getAllUsers();
}

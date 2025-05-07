package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;

public interface UserStorage {
    User getUser(int id);

    User addUser(User user);

    void removeUser(int id);

    User updateUser(User user);
}

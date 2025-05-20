package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        return addUser(userDto);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}

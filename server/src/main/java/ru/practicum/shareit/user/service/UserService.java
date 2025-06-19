package ru.practicum.shareit.user.service;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.exceptions.ConflictException;
import ru.practicum.shareit.error.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.constants.Constants.copyFields;

@Component
@AllArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private UserDtoMapper userDtoMapper;

    public UserDto getUser(int id) {
        return userDtoMapper.toDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("User with id " + id + " not found")));
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto addUser(UserDto userDto) {
        userValidation(userDto, false);
        return userDtoMapper.toDto(userRepository.save(userDtoMapper.toUser(userDto)));
    }

    public void removeUser(int id) {
        userRepository.deleteById(id);
    }

    public UserDto updateUser(UserDto userDto) {
        userValidation(userDto, true);
        UserDto oldUser = getUser(userDto.getId());
        copyFields(oldUser, userDto);
        return userDtoMapper.toDto(userRepository.save(userDtoMapper.toUser(oldUser)));
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
}

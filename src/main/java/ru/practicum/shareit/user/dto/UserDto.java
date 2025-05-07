package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    private String name;
    private String email;

    public static UserDto getUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}

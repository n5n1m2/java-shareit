package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.groups.Add;

@Data
@AllArgsConstructor
public class UserDto {
    private Integer id;
    @NotNull(groups = {Add.class}, message = "The name must not be null.")
    private String name;
    @NotNull(groups = Add.class)
    @Email
    private String email;
}

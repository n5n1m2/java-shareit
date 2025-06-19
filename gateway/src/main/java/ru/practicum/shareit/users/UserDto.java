package ru.practicum.shareit.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validated.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @NotEmpty(groups = Default.class)
    private String name;
    @Email(groups = {Update.class, Default.class})
    @NotEmpty(groups = Default.class)
    private String email;
}

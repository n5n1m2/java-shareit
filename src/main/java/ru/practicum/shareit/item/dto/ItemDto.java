package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.groups.Add;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank(message = "The name must not be empty", groups = Add.class)
    private String name;
    @NotBlank(message = "The description must not be empty", groups = Add.class)
    private String description;
    @NotNull(groups = Add.class)
    private Boolean available;
    private User owner;
    private ItemRequest request;
}

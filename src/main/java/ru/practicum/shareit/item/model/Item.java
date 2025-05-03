package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
public class Item {
    private Integer id;
    @NotBlank(message = "The name must not be empty")
    private String name;
    @NotBlank(message = "The description must not be empty")
    private String description;
    private boolean available;
    @NotNull(message = "Item must have owner")
    private User owner;
    private ItemRequest request;
}
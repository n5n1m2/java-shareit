package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class ItemDto {
    private Integer id;
    @NotBlank(message = "The name must not be empty")
    private String name;
    @NotBlank(message = "The description must not be empty")
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable() != null ? item.getAvailable() : false,
        item.getOwner(),
        item.getRequest() != null ? item.getRequest() : null);
    }
}

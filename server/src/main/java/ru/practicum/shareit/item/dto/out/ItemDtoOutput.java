package ru.practicum.shareit.item.dto.out;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.request.dto.our.ItemRequestDtoOutput;
import ru.practicum.shareit.user.User;

@Data
@AllArgsConstructor
public class ItemDtoOutput {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequestDtoOutput request;
}

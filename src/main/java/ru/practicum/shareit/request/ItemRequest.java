package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
public class ItemRequest {
    private Integer id;
    @NotBlank(message = "The description must not be empty")
    private String description;
    @NotNull(message = "Item must have requester")
    private User requester;
    private LocalDateTime requestDate;
}
package ru.practicum.shareit.items.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {
    @NotEmpty
    private String text;
    private LocalDateTime created;
}

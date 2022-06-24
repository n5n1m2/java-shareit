package ru.practicum.shareit.item.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private int id;
    private int itemId;
    private String text;
    private String authorName;
    private LocalDateTime created;
}

package ru.practicum.shareit.item.dto.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentDtoMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(),
                comment.getItem().getId(),
                comment.getText(),
                comment.getUser().getName(),
                comment.getCreated());
    }
}

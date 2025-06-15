package ru.practicum.shareit.error.exceptions;

public class CommentNoHavePermission extends RuntimeException {
    public CommentNoHavePermission(String message) {
        super(message);
    }
}

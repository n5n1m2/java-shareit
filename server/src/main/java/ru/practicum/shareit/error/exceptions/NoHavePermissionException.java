package ru.practicum.shareit.error.exceptions;

public class NoHavePermissionException extends RuntimeException {
    public NoHavePermissionException(String message) {
        super(message);
    }
}

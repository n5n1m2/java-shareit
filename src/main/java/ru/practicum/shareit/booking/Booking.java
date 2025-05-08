package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Booking {
    private Integer id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull(message = "The item must not be null")
    private Item item;
    @NotNull(message = "The booker must not be null")
    private User booker;
    @NotNull(message = "The status must not be null")
    private BookingStatus status;
}

package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.validation.BookingTimeValidation;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoInput {
    private Integer id;
    @NotNull
    @BookingTimeValidation(message = "start in past")
    private LocalDateTime start;
    @NotNull
    @BookingTimeValidation(message = "end time in past", isEndTime = true)
    private LocalDateTime end;
    @NotNull
    private Integer itemId;
    private Integer bookerId;
    private BookingStatus status;
}

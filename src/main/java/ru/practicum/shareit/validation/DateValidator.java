package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DateValidator implements ConstraintValidator<BookingTimeValidation, LocalDateTime> {
    boolean isEndTime;

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
       return localDateTime.isAfter(LocalDateTime.now().minusSeconds(10));
    }

    @Override
    public void initialize(BookingTimeValidation constraintAnnotation) {
        this.isEndTime = constraintAnnotation.isEndTime();
    }
}

package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateValidator.class})
public @interface BookingTimeValidation {
    String message() default "Дата должна быть после {value}";

    boolean isEndTime() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

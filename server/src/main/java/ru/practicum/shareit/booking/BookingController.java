package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.in.BookingDtoInput;
import ru.practicum.shareit.booking.dto.out.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput createBooking(@RequestBody @Validated BookingDtoInput booking, @RequestHeader(USER_ID_HEADER) Integer userId) {
        return bookingService.createBooking(booking, userId);
    }

    @PatchMapping("/{id}")
    public BookingDtoOutput updateBooking(@PathVariable Integer id,
                                          @RequestParam Boolean approved,
                                          @RequestHeader(USER_ID_HEADER) Integer userId) {
        return bookingService.updateBooking(id, userId, approved);
    }

    @GetMapping("/{id}")
    public BookingDtoOutput getBooking(@PathVariable int id, @RequestHeader(USER_ID_HEADER) Integer userId) {
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping()
    public List<BookingDtoOutput> getBookings(@RequestHeader(USER_ID_HEADER) Integer userId,
                                              @RequestParam(defaultValue = "ALL", required = false) State state) {
        return bookingService.getBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoOutput> getMyBookings(@RequestHeader(USER_ID_HEADER) Integer userId,
                                                @RequestParam(defaultValue = "ALL", required = false) State state) {
        return bookingService.getBookingsForOwner(userId, state);
    }
}

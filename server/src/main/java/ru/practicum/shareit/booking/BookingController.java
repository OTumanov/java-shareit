package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDto findById(@PathVariable Long bookingId,
                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> findAllBookings(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "20") int size) {
        return bookingService.findAllByBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> findAll(@RequestParam(defaultValue = "ALL") String state,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(defaultValue = "0") int from,
                                    @RequestParam(defaultValue = "20") int size) {
        return bookingService.findAllByItemOwner(state, userId, from, size);
    }

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDto dto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto patchBooking(@PathVariable Long bookingId,
                                   @RequestParam Boolean approved,
                                   @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.patchBooking(bookingId, approved, userId);
    }
}

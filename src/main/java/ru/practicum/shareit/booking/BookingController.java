package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingPost;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingMapper;
import ru.practicum.shareit.validation.Create;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private BookingService bookingService;

    @GetMapping("/{bookingId}")
    public BookingDetailedDto findById(@PathVariable Long bookingId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toBookingDetailedDto(bookingService.findById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDetailedDto> findAllBookings(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return BookingMapper.toListDetailedDto(bookingService.findAllByBooker(state, userId));
    }

    @GetMapping("/owner")
    public List<BookingDetailedDto> findAll(@RequestParam(defaultValue = "ALL") String state,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.findAllByItemOwner(state, userId);
    }

    @PostMapping
    public BookingPostResponseDto createBooking(@RequestBody @Validated(Create.class) BookingPost bookingPost,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.createBooking(bookingPost, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patchBooking(@PathVariable Long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.patchBooking(bookingId, approved, userId);
    }
}

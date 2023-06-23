package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.utils.BookingMapper;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("/{bookingId}")
    public BookingDetailedDto findById(@PathVariable Long bookingId,
                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос bookingId={}, userId={}", bookingId, userId);
        return BookingMapper.toBookingDetailedDto(bookingService.findById(bookingId, userId));
    }

    @GetMapping
    public List<BookingDetailedDto> findAllBookings(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestHeader(USER_ID_HEADER) Long userId,
                                                    @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(defaultValue = "10") Integer size) {
        return BookingMapper.toListDetailedDto(bookingService.findAllByBooker(state, userId, from, size));
    }

    @GetMapping("/owner")
    public List<BookingDetailedDto> findAllByItemOwner(@RequestParam(defaultValue = "ALL") String state,
                                                       @RequestHeader(USER_ID_HEADER) Long userId) {
        return BookingMapper.toListDetailedDto(bookingService.findAllByItemOwner(state, userId));
    }

    @PostMapping
    public BookingPostResponseDto createBooking(@RequestBody BookingPostDto bookingPostDto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на бронирование вещи bookingPost={}, userId={}", bookingPostDto, userId);
        return BookingMapper.toBookingPostResponseDto(bookingService.createBooking(bookingPostDto, userId));
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto patchBooking(@PathVariable Long bookingId,
                                           @RequestParam Boolean approved,
                                           @RequestHeader(USER_ID_HEADER) Long userId) {
        log.info("Запрос на обновление bookingId={}, approved={}, userId={}", bookingId, approved, userId);
        return BookingMapper.toBookingResponseDto(bookingService.patchBooking(bookingId, approved, userId));
    }
}

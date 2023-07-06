package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> findById(@PathVariable Long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос по bookingId={}, userId={}", bookingId, userId);
        return bookingClient.findById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllBookings(@RequestParam(defaultValue = "ALL") String state,
                                                  @RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(defaultValue = "20") @PositiveOrZero int size) {
        log.info("Запрос по state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findAllByBooker(state, userId, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> findAllByItemOwner(@RequestParam(defaultValue = "ALL") String state,
                                                     @RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "0") @Min(0) int from,
                                                     @RequestParam(defaultValue = "20") @PositiveOrZero int size) {
        log.info("Запрос от владельца по state={}, userId={}, from={}, size={}", state, userId, from, size);
        return bookingClient.findAllByItemOwner(state, userId, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDto dto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Создание booking={}. userId={}", dto, userId);
        return bookingClient.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchBooking(@PathVariable Long bookingId,
                                               @RequestParam Boolean approved,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Аппрув bookingId={}, approved={}, userId={}", bookingId, approved, userId);
        return bookingClient.patchBooking(bookingId, approved, userId);
    }


}
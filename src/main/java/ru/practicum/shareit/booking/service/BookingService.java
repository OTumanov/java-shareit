package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingDetailed;
import ru.practicum.shareit.booking.model.BookingPost;
import ru.practicum.shareit.booking.model.BookingPostResponse;
import ru.practicum.shareit.booking.model.BookingResponse;

import java.util.List;

public interface BookingService {
    BookingPostResponse createBooking(BookingPost bookingPost, Long userId);

    BookingResponse patchBooking(Long bookingId, Boolean approved, Long userId);

    BookingDetailed findById(Long bookingId, Long userId);

    List<BookingDetailed> findAllByBooker(String state, Long userId);

    List<BookingDetailed> findAllByItemOwner(String state, Long userId);
}

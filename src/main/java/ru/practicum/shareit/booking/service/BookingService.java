package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.model.*;

import java.util.List;

public interface BookingService {
    Booking findById(Long bookingId, Long userId);

    List<Booking> findAllByBooker(String state, Long userId, Integer from, Integer size);

    List<Booking> findAllByItemOwner(String state, Long userId);

    Booking createBooking(BookingPostDto bookingPost, Long userId);

    Booking patchBooking(Long bookingId, Boolean approved, Long userId);

}

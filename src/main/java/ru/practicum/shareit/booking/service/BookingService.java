package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.*;

import java.util.List;

public interface BookingService {
    Booking findById(Long bookingId, Long userId);

    List<Booking> findAllByBooker(String state, Long userId);

    List<Booking> findAllByItemOwner(String state, Long userId);

    Booking createBooking(BookingPost bookingPost, Long userId);

    Booking patchBooking(Long bookingId, Boolean approved, Long userId);

}

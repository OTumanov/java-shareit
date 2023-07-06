package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto dto, Long userId);

    BookingDto patchBooking(Long bookingId, Boolean approved, Long userId);

    BookingDto findById(Long bookingId, Long userId);

    List<BookingDto> findAllByBooker(String state, Long userId, int from, int size);

    List<BookingDto> findAllByItemOwner(String state, Long userId, int from, int size);
}
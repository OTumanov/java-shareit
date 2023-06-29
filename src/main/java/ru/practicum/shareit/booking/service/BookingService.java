package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking findById(Long bookingId, Long userId);

    List<BookingDetailedDto> findAllByBooker(String state, Long userId, Integer from, Integer size);

    List<Booking> findAllByItemOwner(String state, Long userId, Integer from, Integer size);

    BookingPostResponseDto createBooking(BookingPostDto bookingPost, Long userId);

    BookingResponseDto patchBooking(Long bookingId, Boolean approved, Long userId);

}

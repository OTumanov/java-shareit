package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.BookingDetailed;
import ru.practicum.shareit.booking.model.BookingPost;
import ru.practicum.shareit.booking.model.BookingPostResponse;
import ru.practicum.shareit.booking.model.BookingResponse;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    @Override
    public BookingDetailed findById(Long bookingId, Long userId) {
        return null;
    }

    @Override
    public List<BookingDetailed> findAllByBooker(String state, Long userId) {
        return null;
    }

    @Override
    public List<BookingDetailed> findAllByItemOwner(String state, Long userId) {
        return null;
    }

    @Override
    public BookingPostResponse createBooking(BookingPost bookingPost, Long userId) {
        return null;
    }

    @Override
    public BookingResponse patchBooking(Long bookingId, Boolean approved, Long userId) {
        return null;
    }
}

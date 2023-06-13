package ru.practicum.shareit.booking.utils;

import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDetailed;

public class BookingMapper {
    public static BookingDetailed toBookingDetailed(Booking booking) {
        return BookingDetailed.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .name(booking.getItem().getName())
                .build();
    }

    public static BookingDetailedDto toBookingDetailedDto(Booking booking) {
        return BookingDetailedDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .name(booking.getItem().getName())
                .build();
    }
}

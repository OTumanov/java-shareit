package ru.practicum.shareit.booking.utils;

import ru.practicum.shareit.booking.dto.BookingDetailedDto;
import ru.practicum.shareit.booking.dto.BookingPostResponseDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingResponseDto toBookingResponseDto(Booking booking) {

        return BookingResponseDto.builder()
                .id(booking.getId())
                .status(booking.getStatus())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .name(booking.getItem().getName())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }

    public static BookingPostResponseDto toBookingPostResponseDto(Booking booking) {

        return BookingPostResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(String.valueOf(booking.getStatus()))
                .booker(booking.getBooker())
                .item(booking.getItem())
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

    public static List<BookingDetailedDto> toListDetailedDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDetailedDto).collect(Collectors.toList());
    }
}

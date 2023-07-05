package ru.practicum.shareit.booking.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;


import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static Booking toModel(BookingDto dto, Item item, User user) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingDto toBookingDtoFromBooking(Booking booking, User booker, Item item) {
        if (booking == null) return null;
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(booking.getBooker());
        dto.setItem(booking.getItem());
        dto.setName(booking.getItem().getName());
        return dto;
    }

    public static BookingDto toBookingDtoFromBooking(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());
        dto.setBooker(booking.getBooker());
        dto.setItem(booking.getItem());
        dto.setName(booking.getItem().getName());
        return dto;
    }

    public static List<BookingDto> toListDetailedDto(List<Booking> bookings) {
        return bookings.stream().map(BookingMapper::toBookingDtoFromBooking).collect(Collectors.toList());
    }

    public static BookingDto bookingDto(Booking booking) {
        if (booking == null) return null;
        BookingDto dto = new BookingDto();
        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setBooker(booking.getBooker());
        dto.setItem(booking.getItem());
        dto.setName(booking.getItem().getName());
        dto.setStatus(booking.getStatus());
        return dto;
    }


}
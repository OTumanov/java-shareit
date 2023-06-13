package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponseDto {
    private Long id;
    private BookingStatus status;
    private User booker;
    private Item item;
    private String name;
}
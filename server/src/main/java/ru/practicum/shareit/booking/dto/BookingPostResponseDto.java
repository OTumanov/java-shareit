package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookingPostResponseDto {
    private Long id;
    private Item item;
    private LocalDateTime start;
    private LocalDateTime end;
}
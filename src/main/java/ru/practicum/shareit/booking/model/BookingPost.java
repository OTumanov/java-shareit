package ru.practicum.shareit.booking.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingPost {
    private Long id;
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}

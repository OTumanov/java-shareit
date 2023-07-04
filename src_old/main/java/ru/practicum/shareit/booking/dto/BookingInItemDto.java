package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.utils.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BookingInItemDto {
    private Long id;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}

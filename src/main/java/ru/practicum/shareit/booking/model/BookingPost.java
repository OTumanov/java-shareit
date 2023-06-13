package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPost {
    private Long id;
    private Long itemId;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime start;

    @FutureOrPresent(groups = {Create.class})
    private LocalDateTime end;
}

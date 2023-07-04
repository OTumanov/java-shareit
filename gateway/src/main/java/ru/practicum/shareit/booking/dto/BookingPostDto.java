package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingPostDto {
    private Long id;
    private Long itemId;
    @FutureOrPresent(groups = {Create.class})
    @NotNull
    private LocalDateTime start;
    @FutureOrPresent(groups = {Create.class})
    @NotNull
    private LocalDateTime end;
}
package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private Long itemId;
    private Long bookerId;
    @FutureOrPresent(groups = {Create.class})
    @NotNull
    private LocalDateTime start;
    @FutureOrPresent(groups = {Create.class})
    @NotNull
    private LocalDateTime end;
}

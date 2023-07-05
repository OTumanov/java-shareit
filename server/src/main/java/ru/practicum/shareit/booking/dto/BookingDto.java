package ru.practicum.shareit.booking.dto;


import lombok.*;
import ru.practicum.shareit.booking.utils.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
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
    private BookingStatus status;
    private User booker;
    private Item item;
    private String name;
}



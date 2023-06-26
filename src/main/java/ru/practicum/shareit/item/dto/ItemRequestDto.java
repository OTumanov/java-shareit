package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;

    @NotBlank
    private String description;

    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}
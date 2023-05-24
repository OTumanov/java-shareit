package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotNull(message = "Название не может быть пустым")
    private String name;
    @NotNull(message = "Название не может быть пустым")
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
}

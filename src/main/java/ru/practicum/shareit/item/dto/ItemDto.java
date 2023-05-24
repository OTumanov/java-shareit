package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {
    private Long id;
    @NotEmpty(message = "Название не может быть пустым")
    private String name;
    @NotEmpty(message = "Описание не может быть пустым")
    private String description;
    private Boolean available;
    @NotEmpty(message = "Владелец не может быть пустым")
    private Long owner;
    private Long request;
}

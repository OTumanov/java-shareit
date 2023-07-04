package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemInRequestDto;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestWithItemsDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemInRequestDto> items;
}
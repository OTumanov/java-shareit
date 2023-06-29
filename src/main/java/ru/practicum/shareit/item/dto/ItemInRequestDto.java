package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemInRequestDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private Long owner;
}
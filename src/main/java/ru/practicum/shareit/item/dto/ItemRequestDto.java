package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@ToString
public class ItemRequestDto {
    private Long id;

    @NotBlank
    private String description;
    private Long requestId;
    private UserDto requester;
    private LocalDateTime created;
    private List<ItemDto> items;
}
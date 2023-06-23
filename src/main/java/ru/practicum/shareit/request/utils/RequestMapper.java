package ru.practicum.shareit.request.utils;

import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.utils.UserMapper;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(UserMapper.toDto(itemRequest.getRequester()))
                .items(itemRequest.getItems() != null ? itemRequest.getItems()
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    public static ItemRequest fromDto(ItemRequestDto itemRequestDto) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .build();
    }
}
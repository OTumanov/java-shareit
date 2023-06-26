package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(long userId, long requestId);

    List<RequestWithItemsDto> findAllByUserId(long userId);

    List<ItemRequestDto> getAllRequest(long userId, int from, int size);

}
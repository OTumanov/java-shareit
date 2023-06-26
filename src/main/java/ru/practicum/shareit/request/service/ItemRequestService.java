package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    List<RequestWithItemsDto> findAllByUserId(Long userId);

    List<RequestWithItemsDto> getAllRequest(Long userId, int from, int size);

    RequestWithItemsDto findById(Long requestId, Long userId);
}
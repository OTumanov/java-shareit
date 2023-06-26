package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(Long userId, Long requestId);

    List<RequestWithItemsDto> findAllByUserId(Long userId);

    List<RequestWithItemsDto> getAllRequest(Long userId, int from, int size);

    RequestWithItemsDto findById(Long requestId, Long userId);
}
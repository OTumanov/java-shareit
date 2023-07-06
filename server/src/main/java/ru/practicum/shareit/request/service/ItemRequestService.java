package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface ItemRequestService {

    RequestDto createRequest(RequestDto dto, Long userId);

    List<RequestDto> findAllByUserId(Long userId);

    List<RequestDto> findAll(int from, int size, Long userId);

    RequestDto findById(Long requestId, Long userId);
}
package ru.practicum.shareit.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {

    @Transactional
    PostResponseRequestDto addRequest(Long userId, PostRequestDto postRequestDto);

    List<RequestWithItemsDto> findAllByUserId(Long userId);

    List<RequestWithItemsDto> getAllRequest(Long userId, int from, int size);

    RequestWithItemsDto findById(Long requestId, Long userId);

    List<RequestWithItemsDto> findAll(int from, int size, Long userId);
}
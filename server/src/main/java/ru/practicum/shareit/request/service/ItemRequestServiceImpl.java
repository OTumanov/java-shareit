package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.model.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.utils.RequestMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    public static final Sort SORT = Sort.by("created").descending();

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    @Transactional
    public RequestDto createRequest(RequestDto dto, Long userId) {
        checkUserFound(userId);
        Request request = RequestMapper.toModel(dto, userId);
        request = requestRepository.save(request);
        return RequestMapper.toDto(request);
    }

    @Override
    public List<RequestDto> findAllByUserId(Long userId) {
        checkUserFound(userId);
        List<Request> requests = requestRepository.findRequestByRequestorOrderByCreatedAsc(userId);
        return RequestMapper.toRequestDtoList(requests, itemRepository);
    }

    @Override
    public List<RequestDto> findAll(int from, int size, Long userId) {
        checkUserFound(userId);
        Pageable pageable = PageRequest.of(from / size, size, SORT);
        Page<Request> requests = requestRepository.findAll(userId, pageable);
        return RequestMapper.toRequestDtoList(requests, itemRepository);
    }

    @Override
    public RequestDto findById(Long requestId, Long userId) {
        checkUserFound(userId);
        Request request = requestRepository.findById(requestId).orElseThrow();
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        return RequestMapper.toRequestDto(request, items);
    }


    private void checkUserFound(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Нет такого пользователя!"));
    }
}
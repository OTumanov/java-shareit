package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestsRepository;
import ru.practicum.shareit.request.utils.RequestMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserMapper;


import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestsRepository requestsRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userService.findUserById(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(user)
                .build();
        return RequestMapper.toDto(requestsRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        UserDto userDto = UserMapper.toDto(userService.findUserById(userId));
        ItemRequest itemRequest = requestsRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("request с id = %d не найден", requestId)));
        itemRequest.setItems(itemRepository.findAllByItemRequestId(itemRequest.getId()));
        ItemRequestDto itemRequestDto = RequestMapper.toDto(itemRequest);
        itemRequestDto.setRequester(userDto);
        return itemRequestDto;
    }

    @Override
    public List<RequestWithItemsDto> findAllByUserId(long userId) {
        List<ItemRequest> requests = requestsRepository.findItemRequestByRequesterOrderByCreatedDesc(userService.findUserById(userId));
        return RequestMapper.toRequestWithItemsDtoList(requests, itemRepository);
    }

    @Override
    public List<ItemRequestDto> getAllRequest(long userId, int from, int size) {
        if(from < 0 || size < 0) {
            throw new ValidationException("Некорректные параметры");
        }
        userService.findUserById(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
        return requestsRepository.findAllByRequesterIdIsNot(userId, page).stream()
                .peek(itemRequest -> itemRequest.setItems(itemRepository.findAllByItemRequestId(itemRequest.getId())))
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());

    }
}
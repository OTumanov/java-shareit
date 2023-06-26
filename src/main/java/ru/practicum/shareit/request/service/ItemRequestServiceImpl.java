package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestsRepository;
import ru.practicum.shareit.request.utils.RequestMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storge.UserRepository;

import javax.validation.ValidationException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestsRepository requestsRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto itemRequestDto) {
        User user = userService.findUserById(userId);
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(user)
                .build();

        return RequestMapper.toDto(requestsRepository.save(itemRequest));
    }

    @Override
    public List<RequestWithItemsDto> findAllByUserId(Long userId) {
        List<ItemRequest> requests = requestsRepository.findItemRequestByRequesterOrderByCreatedDesc(userService.findUserById(userId));
        return RequestMapper.toRequestWithItemsDtoList(requests, itemRepository);
    }

    @Override
    public List<RequestWithItemsDto> getAllRequest(Long userId, int from, int size) {
        checkPageAndSize(from, size);
        userRepository.findById(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created"));
        Page<ItemRequest> requests = requestsRepository.findAll(userId, pageable);

        return RequestMapper.toRequestWithItemsDtoList(requests, itemRepository);
    }

    @Override
    public RequestWithItemsDto findById(Long requestId, Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException(String.format("user с id = %d не найден", userId)));
        ItemRequest request = requestsRepository.findById(requestId).orElseThrow(() -> new NotFoundException(String.format("request с id = %d не найден", requestId)));
        List<Item> items = itemRepository.findAllByItemRequestId(requestId);

        return RequestMapper.toRequestWithItemsDto(request, items);
    }

    private void checkPageAndSize(int from, int size) {
        if (from < 0 || size < 0) {
            throw new ValidationException("Некорректные параметры");
        }
    }
}
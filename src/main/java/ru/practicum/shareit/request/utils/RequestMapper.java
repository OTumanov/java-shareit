package ru.practicum.shareit.request.utils;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.utils.UserMapper;

import java.util.ArrayList;
import java.util.List;
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

    public static RequestWithItemsDto toRequestWithItemsDto(ItemRequest request, List<Item> items) {
        List<ItemInRequestDto> itemDtos = ItemMapper.toRequestItemDtoList(items);
        RequestWithItemsDto dto = new RequestWithItemsDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(itemDtos);
        return dto;
    }

    public static List<RequestWithItemsDto> toRequestWithItemsDtoList(Page<ItemRequest> requests,
                                                                      ItemRepository repository) {
        return requests.stream()
                .map((ItemRequest request) -> {
                    List<Item> items = repository.findAllByItemRequestId(request.getId());
                    return RequestMapper.toRequestWithItemsDto(request, items);
                }).collect(Collectors.toList());
    }

    public static List<RequestWithItemsDto> toRequestWithItemsDtoList(List<ItemRequest> requests, ItemRepository repository) {
        List<RequestWithItemsDto> result = new ArrayList<>();
        if (requests != null && !requests.isEmpty()) {
            for (ItemRequest request : requests) {
                List<Item> items = repository.findAllByItemRequestId(request.getId());
                System.out.println(items);
                RequestWithItemsDto requestDto = RequestMapper.toRequestWithItemsDto(request, items);
                result.add(requestDto);
            }
        }
        return result;
    }
}
package ru.practicum.shareit.request.utils;

import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dto.PostRequestDto;
import ru.practicum.shareit.request.dto.PostResponseRequestDto;
import ru.practicum.shareit.request.dto.RequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static RequestWithItemsDto toRequestWithItemsDto(ItemRequest request, List<Item> items) {
        List<ItemInRequestDto> itemDtos = ItemMapper.toRequestItemDtoList(items);
        RequestWithItemsDto dto = new RequestWithItemsDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(itemDtos);

        return dto;
    }

    public static List<RequestWithItemsDto> toRequestWithItemsDtoList(Page<ItemRequest> requests, ItemRepository repository) {

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
                RequestWithItemsDto requestDto = RequestMapper.toRequestWithItemsDto(request, items);
                result.add(requestDto);
            }
        }

        return result;
    }

    public static ItemRequest toModel(PostRequestDto dto, Long requestor) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(dto.getDescription());
        itemRequest.setRequester(requestor);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static PostResponseRequestDto toPostResponseDto(ItemRequest itemRequest) {
        PostResponseRequestDto dto = new PostResponseRequestDto();
        dto.setId(itemRequest.getId());
        dto.setDescription(itemRequest.getDescription());
        dto.setCreated(itemRequest.getCreated());
        return dto;
    }
}
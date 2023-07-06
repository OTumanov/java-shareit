package ru.practicum.shareit.request.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {

    public static Request toModel(RequestDto dto, Long requestor) {
        Request request = new Request();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public static RequestDto toDto(Request request) {
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        return dto;
    }

    public static RequestDto toRequestDto(Request request, List<Item> items) {
        List<ItemDto> itemDtos = ItemMapper.toItemDtoList(items);
        System.out.println("itemsDto " + itemDtos);
        RequestDto dto = new RequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(itemDtos);
        return dto;
    }

    public static List<RequestDto> toRequestDtoList(Page<Request> requests,
                                                    ItemRepository repository) {
        return requests.stream()
                .map((Request request) -> {
                    List<Item> items = repository.findAllByRequestId(request.getId());
                    return RequestMapper.toRequestDto(request, items);
                }).collect(Collectors.toList());
    }

    public static List<RequestDto> toRequestDtoList(List<Request> requests, ItemRepository repository) {
        List<RequestDto> result = new ArrayList<>();
        if (requests != null && !requests.isEmpty()) {
            for (Request request : requests) {
                List<Item> items = repository.findAllByRequestId(request.getId());
                RequestDto requestDto = RequestMapper.toRequestDto(request, items);
                result.add(requestDto);
            }
        }

        System.out.println(result);
        return result;
    }
}
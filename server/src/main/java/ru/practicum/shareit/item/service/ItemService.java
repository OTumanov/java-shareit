package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto dto, Long userId);

    CommentDto createComment(CommentDto dto, Long itemId, Long userId);

    ItemDto updateItem(ItemDto dto, Long itemId, Long userId);

    ItemDto findItemById(Long itemId, Long userId);

    List<ItemDto> findAllItems(Long userId, int from, int size);

    List<ItemDto> findItemsByRequest(String text, Long userId, int from, int size);
}
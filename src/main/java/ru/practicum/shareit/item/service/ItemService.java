package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateCommentFromDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> findAllItemsByUserId(Long userId, Integer from, Integer size);

    ItemDto createItem(ItemDto itemDto, Long userId);

    Item updateItem(Long itemId, Long userId, Item item);

    void deleteItem(Long itemId);

    List<Item> search(String text, Integer from, Integer size);

    CommentDto createComment(CreateCommentFromDto commentDto, Long itemId, Long userId);

    void checkItem(ItemDto itemDto, Long userId);
}

package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(Long itemId);

    List<Item> getAllItems(Long userId);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    void deleteItem(Long itemId);

    List<ItemDto> searchItems(String text, Long userId);
}

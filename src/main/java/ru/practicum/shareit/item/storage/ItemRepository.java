package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {
    ItemDto getItemById(Long itemId);

    List<ItemDto> getAllItems(Long userId);

    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto);

    void deleteItem(Long itemId);

    List<ItemDto> searchItems(String text, Long userId);
}

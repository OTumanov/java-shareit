package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long itemId, Long userId);

    List<ItemDto> findAllItemsByUserId(Long userId);

    Item createItem(Item item, Long userId);

    Item updateItem(Long itemId, Long userId, Item item);

    void deleteItem(Long itemId);

    List<Item> search(String text, Long userId);
}

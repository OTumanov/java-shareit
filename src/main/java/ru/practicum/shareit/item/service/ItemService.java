package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item getItemById(Long itemId);

    List<Item> findAllItemsByUserId(Long userId);

    Item createItem(Item item, Long userId);

    Item updateItem(Long itemId, Long userId, Item item);

    void deleteItem(Long itemId);

    List<Item> search(String text, Long userId);
}

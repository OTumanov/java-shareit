package ru.practicum.shareit.item.storage.inMemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.utils.ItemMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final UserService userService;
    Map<Long, Item> allItems = new HashMap<>();
    Long nextId = 0L;

    @Override
    public ItemDto getItemById(Long itemId) {
        return ItemMapper.toItemDto(allItems.get(itemId));
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<ItemDto> result = new ArrayList<>();
        for (Item i : allItems.values()) {
            result.add(ItemMapper.toItemDto(i));
        }
        return result;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, Long userId) {
        if (userService.getUserById(userId) == null) {
            throw new IllegalArgumentException("Пользователь не найден!");
        }
        for (Item i : allItems.values()) {
            if (i.getName().equals(itemDto.getName())) {
                throw new IllegalArgumentException("Вещь с именем = " + itemDto.getName() + " уже существует!");
            }
        }

        Item newItem = ItemMapper.toItem(itemDto);
        newItem.setId(++nextId);
        newItem.setOwner(userId);
        allItems.put(newItem.getId(), newItem);
        return ItemMapper.toItemDto(newItem);
    }


    @Override
    public ItemDto updateItem(Long itemId, ItemDto itemDto) {
        return null;
    }

    @Override
    public void deleteItem(Long itemId) {

    }
}

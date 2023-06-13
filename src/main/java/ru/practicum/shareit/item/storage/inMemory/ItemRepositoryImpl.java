package ru.practicum.shareit.item.storage.inMemory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.model.AccessException;
import ru.practicum.shareit.exceptions.model.ConflictException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
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
    public Item getItemById(Long itemId) {
        return allItems.get(itemId);
    }

    @Override
    public List<Item> getAllItems(Long userId) {
        List<Item> result = new ArrayList<>();
        for (Item i : allItems.values()) {
            if (i.getOwnerId().equals(userId)) {
                result.add(i);
            }
        }
        return result;
    }

    @Override
    public Item createItem(Item item, Long userId) {
        userService.getUserById(userId);
        checkItemAvailable(item);
        checkItemName(item);
        checkDescription(item);
        item.setId(++nextId);
        item.setOwnerId(userId);
        allItems.put(item.getId(), item);

        return allItems.get(item.getId());
    }

    @Override
    public Item updateItem(Long itemId, Long userId, Item item) {
        checkItem(itemId);
        checkOwnerOfItem(itemId, userId);

        Item updatedItem = allItems.get(itemId);

        if (!(item.getName() == null)) {
            updatedItem.setName(item.getName());
        }

        if (!(item.getDescription() == null)) {
            updatedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            updatedItem.setAvailable(item.getAvailable());
        }

        return updatedItem;
    }

    @Override
    public void deleteItem(Long itemId) {
        checkItem(itemId);
        allItems.remove(itemId);
    }

    @Override
    public List<Item> searchItems(String text, Long userId) {
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            List<Item> result = new ArrayList<>();

            for (Item i : allItems.values()) {
                if (i.getName().toLowerCase().contains(text.toLowerCase())
                        || i.getDescription().toLowerCase().contains(text.toLowerCase())
                        && i.getAvailable()) {
                    result.add(i);
                }
            }
            return result;
        }
    }

    private void checkItem(Long itemId) {
        if (allItems.get(itemId) == null) {
            throw new NotFoundException("Такая вещь не найдена!");
        }
    }

    private void checkItemAvailable(Item item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Вещь не доступна!");
        }
    }

    private void checkItemName(Item item) {
        if (item.getName() == null) {
            throw new ConflictException("Вещь не может быть безымянной!");
        }
        if (item.getName().isEmpty() || item.getName().isBlank()) {
            throw new ValidationException("Поле имени должно быть заполнено!");
        }
    }

    private void checkDescription(Item item) {
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Отсутствует описание!");
        }
    }

    private void checkOwnerOfItem(Long itemDto, Long userId) {
        if (!allItems.get(itemDto).getOwnerId().equals(userId)) {
            throw new AccessException("Вещь не принадлежит данному пользователю!");
        }
    }
}

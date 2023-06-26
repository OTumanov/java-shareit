package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;


import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("select it " +
            "from Item as it " +
            "where (upper(it.name) like upper('%'||?1||'%') " +
            "or upper(it.description) like upper('%'||?1||'%')) " +
            "and it.available = true")
    List<Item> search(String text, Pageable page);

    List<Item> findAllByOwnerId(Long ownerId, Pageable page);

    List<Item> findAllByItemRequest(ItemRequest itemRequest);

    List<Item> findAllByItemRequest(Long requestId);
}

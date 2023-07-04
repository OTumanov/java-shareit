package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Pageable page);

    List<Item> findAllByItemRequestId(Long requestId);

    @Query("select it " +
            "from Item as it " +
            "where (upper(it.name) like upper('%'||?1||'%') " +
            "or upper(it.description) like upper('%'||?1||'%')) " +
            "and it.available = true")
    List<Item> search(String text, Pageable page);

    @Query("select item from Item item " +
            "where item.available = true and " +
            "upper(item.name) like upper(concat('%', ?1, '%')) " +
            "or upper(item.description) like upper(concat('%', ?1, '%')) " +
            "and item.available = true")
    List<Item> searchAvailableItems(String text, Pageable page);

}

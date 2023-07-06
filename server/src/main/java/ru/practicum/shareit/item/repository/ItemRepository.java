package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByOwner(Long userId, Pageable page);

    @Query("select item from items item " +
            "where item.available = true and " +
            "upper(item.name) like upper(concat('%', ?1, '%')) " +
            "or upper(item.description) like upper(concat('%', ?1, '%')) " +
            "and item.available = true")
    List<Item> searchAvailableItems(String text, Pageable page);
}
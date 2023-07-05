package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query(" select i from items i " +
            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')) and i.available = true")
    Page<Item> search(String text, Pageable pageable);

//    @Query("select it " +
//            "from items as it " +
//            "where (upper(it.name) like upper('%'||?1||'%') " +
//            "or upper(it.description) like upper('%'||?1||'%')) " +
//            "and it.available = true")
//    Page<Item> search(String text, Pageable page);

    @Query("select i from items i where i.owner = ?1")
    Page<Item> findAll(Long userId, Pageable pageable);

    List<Item> findAllByRequestId(Long requestId);

    List<Item> findAllByOwner(Long userId, Pageable page);

    @Query("select item from items item " +
            "where item.available = true and " +
            "upper(item.name) like upper(concat('%', ?1, '%')) " +
            "or upper(item.description) like upper(concat('%', ?1, '%')) " +
            "and item.available = true")
    List<Item> searchAvailableItems(String text, Pageable page);
}
package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
public interface ItemRepository extends JpaRepository<Item, Long> {

//    @Query(" select i from Item i " +
//            "where upper(i.name) like upper(concat('%', ?1, '%')) " +
//            "   or upper(i.description) like upper(concat('%', ?1, '%'))")

    @Query("select it " +
            "from Item as it " +
            "where (upper(it.name) like upper('%'||?1||'%') " +
            "or upper(it.description) like upper('%'||?1||'%')) " +
            "and it.available = true")
    List<Item> search(String text);

    List<Item> findAllByOwnerId(Long ownerId);
}

package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface RequestsRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long userId);

    List<ItemRequest> findItemRequestByRequesterOrderByCreatedDesc(User user);

    List<ItemRequest> findAllByRequesterIdIsNot(long userId, Pageable page);

    Page<ItemRequest> findAllByRequesterId(long userId, Pageable pageable);

    @Query("select r from ItemRequest r where r.requester.id <> ?1")
    Page<ItemRequest> findAll(Long userId, Pageable pageable);
}
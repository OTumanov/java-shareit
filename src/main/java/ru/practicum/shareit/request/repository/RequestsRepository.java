package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface RequestsRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc(long userId);

    //    List<ItemRequest> findRequestByRequestorOrderByCreatedDesc(long userId);
    List<ItemRequest> findItemRequestByRequesterOrderByCreatedDesc(User user);

    List<ItemRequest> findAllByRequesterIdIsNot(long userId, Pageable page);
}
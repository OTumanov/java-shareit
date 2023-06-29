package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestsRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findItemRequestByRequesterOrderByCreatedDesc(Long userId);

    Page<ItemRequest> findAllByRequesterIsNot(Long userId, Pageable pageable);
}
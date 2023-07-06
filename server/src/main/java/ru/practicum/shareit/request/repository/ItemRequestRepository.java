package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<Request, Long> {

    @Query("select r from requests r where r.requestor <> ?1")
    Page<Request> findAll(Long userId, Pageable pageable);

    List<Request> findRequestByRequestorOrderByCreatedDesc(Long userId);
}
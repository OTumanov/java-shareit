package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.utils.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndEndIsBefore(Long itemId, Long bookerId, LocalDateTime now, Pageable pageable);
    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);


    Page<Booking> findBookingByItemOwnerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findBookingByItemOwnerIdAndEndIsBefore(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingByItemOwnerIdAndStartIsAfter(Long bookerId, LocalDateTime now, Pageable pageable);

    Page<Booking> findBookingByItemOwnerId(Long bookerId, Pageable pageable);

    Page<Booking> findByBookerId(Long bookerId, Pageable pageable);

    List<Booking> findAllBookingsByItemId(Long id);

    @Query("select b from Booking b " +
            "where b.booker.id = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start desc ")
    Page<Booking> findByBookerIdCurrent(Long userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            "where b.item.ownerId = ?1 " +
            "and b.start < ?2 " +
            "and b.end > ?2 " +
            "order by b.start asc")
    List<Booking> findBookingsByItemOwnerCurrent(Long userId, LocalDateTime now, Pageable pageable);

    @Query("select b from Booking b " +
            " where b.item.id = ?1 " +
            " and b.booker.id = ?2" +
            " and b.end < ?3")
    List<Booking> findBookingsForAddComments(Long itemId, Long userId, LocalDateTime now);

}
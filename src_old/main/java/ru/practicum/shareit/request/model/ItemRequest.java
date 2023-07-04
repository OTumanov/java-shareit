package ru.practicum.shareit.request.model;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "requests")
@Data
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@SuppressWarnings("ALL")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    //    @ManyToOne
    @JoinColumn(name = "requester_id")
    private Long requester;

    @CreationTimestamp
    private LocalDateTime created;

    @Transient
    private List<Item> items;
}
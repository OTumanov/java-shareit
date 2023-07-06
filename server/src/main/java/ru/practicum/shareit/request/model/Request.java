package ru.practicum.shareit.request.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "requests")
public class Request {
    @Id
    @Column(name = "request_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", nullable = false, length = 512)
    private String description;
    @Column(name = "requestor", nullable = false)
    private Long requestor;
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "comments")
public class Comment {
    public static final int MAX_TEXT_LENGTH = 512;
    public static final String TEXT_COLUMN_NAME = "text";
    public static final String ITEM_COLUMN_NAME = "item";
    public static final String OWNER_COLUMN_NAME = "owner";
    public static final String ID_COLUMN_NAME = "comment_id";

    @Id
    @Column(name = ID_COLUMN_NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = TEXT_COLUMN_NAME, nullable = false, length = MAX_TEXT_LENGTH)
    private String text;
    @ManyToOne
    @JoinColumn(name = Item.ID_COLUMN_NAME)
    private Item item;
    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    private LocalDateTime created;
}
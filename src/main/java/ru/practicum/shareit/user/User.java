package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Класс User в пакете user будет совсем простым. Всего три поля:
 * id — уникальный идентификатор пользователя;
 * name — имя или логин пользователя;
 * email — адрес электронной почты (учтите, что два пользователя не могут иметь одинаковый адрес электронной почты).
 */

@Data
@Builder
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}



package ru.practicum.shareit.user.storge;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User getUserById(Long id);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);
}

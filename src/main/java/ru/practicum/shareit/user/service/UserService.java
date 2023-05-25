package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    User getUserById(Long id);

    List<User> getAllUsers();

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUser(Long userId);
}

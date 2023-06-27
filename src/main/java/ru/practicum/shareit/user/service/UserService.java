package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    User findUserById(Long id);

    List<User> findAllUsers();

    User createUser(User user);

    User updateUser(Long userId, User user);

    void deleteUserById(Long userId);
}

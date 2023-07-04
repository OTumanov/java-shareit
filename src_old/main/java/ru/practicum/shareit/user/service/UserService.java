package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
public interface UserService {
    User findUserById(Long id);

    List<User> findAllUsers();

    User createUser(User user);

    @Transactional
    UserDto updateUser(long userId, UserDto userDto);

    void deleteUserById(Long userId);
}

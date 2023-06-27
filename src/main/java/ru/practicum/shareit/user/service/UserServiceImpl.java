package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storge.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findUserById(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Нет такого пользователя"));
    }

    @Override
    public List<User> findAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) {
        if (user.getName().isBlank() || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Некорректные данные");
        }
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long userId, User user) {
        User updateUser = patchUser(userId, user);

        return userRepository.save(updateUser);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    private User patchUser(Long userId, User user) {
        User patchedUser = userRepository.getById(userId);
        String name = user.getName();
        String newEmail = user.getEmail();
        String oldEmail = patchedUser.getEmail();
        if (name != null && !name.isBlank()) {
            patchedUser.setName(name);
        }
        if (!oldEmail.equals(newEmail) && newEmail != null && !newEmail.isBlank()) {
            patchedUser.setEmail(newEmail);
        }

        return patchedUser;
    }
}

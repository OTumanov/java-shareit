package ru.practicum.shareit.user.storge.inMemory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.model.ConflictException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storge.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
class UserRepositoryImpl implements UserRepository {
    Map<Long, User> allUsers = new HashMap<>();
    Long nextId = 0L;

    @Override
    public User getUserById(Long id) {
        if (!allUsers.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return allUsers.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers.values());
    }

    @Override
    public User createUser(User user) {
        for (User u : allUsers.values()) {
            checkUserWithEmail(u, user);
        }
        user.setId(++nextId);
        allUsers.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(Long userId, User user) {
        getUserById(userId);
        allUsers.put(userId, updateUserFromNewUser(userId, user));

        return getUserById(userId);
    }

    @Override
    public void deleteUser(Long userId) {
        userFound(userId);
        allUsers.remove(userId);
    }


    private void userFound(Long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void checkUserWithEmail(User user1, User user2) {
        if (user1.getEmail().equals(user2.getEmail())) {
            throw new ConflictException("Пользователь с email = " + user2.getEmail() + " уже существует!");
        }
    }

    private void checkUserDuplicateEmail(User user1, Long userId, User user2) {
        if (user1.getEmail().equals(user2.getEmail()) && (!user1.getId().equals(userId))) {
            throw new IllegalArgumentException("Пользователь с email = " + user2.getEmail() + " уже существует!");
        }
    }

    private User updateUserFromNewUser(Long userId, User user) {
        User updatedUser = allUsers.get(userId);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            for (User u : allUsers.values()) {
                checkUserDuplicateEmail(u, userId, user);
            }
            updatedUser.setEmail(user.getEmail());
        }
        updatedUser.setId(userId);

        return updatedUser;
    }
}

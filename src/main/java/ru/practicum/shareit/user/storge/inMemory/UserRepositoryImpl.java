package ru.practicum.shareit.user.storge.inMemory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.model.ConflictException;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storge.UserRepository;
import ru.practicum.shareit.user.utils.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
class UserRepositoryImpl implements UserRepository {
    Map<Long, User> allUsers = new HashMap<>();
    Long nextId = 0L;

    private void userFound(Long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    private void checkUserWithEmail(User user, UserDto userDto) {
        if (user.getEmail().equals(userDto.getEmail())) {
            throw new ConflictException("Пользователь с email = " + userDto.getEmail() + " уже существует!");
        }
    }

    private void checkUserDuplicateEmail(User user, Long userId, UserDto userDto) {
        if (user.getEmail().equals(userDto.getEmail()) && (!user.getId().equals(userId))) {
            throw new IllegalArgumentException("Пользователь с email = " + userDto.getEmail() + " уже существует!");
        }
    }

    private User updateUserFromDtoUser(Long userId, UserDto userDto) {
        User updatedUser = allUsers.get(userId);

        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            for (User u : allUsers.values()) {
                checkUserDuplicateEmail(u, userId, userDto);
            }
            updatedUser.setEmail(userDto.getEmail());
        }
        updatedUser.setId(userId);

        return updatedUser;
    }

    @Override
    public UserDto getUserById(Long id) {
        userFound(id);
        return UserMapper.toDto(allUsers.get(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> result = new ArrayList<>();
        for (User u : allUsers.values()) {
            result.add(UserMapper.toDto(u));
        }
        return result;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        for (User u : allUsers.values()) {
            checkUserWithEmail(u, userDto);
        }
        User newUser = UserMapper.toUser(userDto);
        newUser.setId(++nextId);
        allUsers.put(newUser.getId(), newUser);

        return UserMapper.toDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        userFound(userId);
        User updatedUser = updateUserFromDtoUser(userId, userDto);
        allUsers.put(updatedUser.getId(), updatedUser);

        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        userFound(userId);
        allUsers.remove(userId);
    }
}

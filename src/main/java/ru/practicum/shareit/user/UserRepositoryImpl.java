package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    Map<Long, User> allUsers = new HashMap<>();
    Long nextId = 0L;

    @Override
    public UserDto getUserById(Long id) {
        if (!allUsers.containsKey(id)) {
            throw new IllegalArgumentException("Пользователь с id = " + id + " не найден");
        }
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
            if (u.getEmail().equals(userDto.getEmail())) {
                throw new IllegalArgumentException("Пользователь с email = " + userDto.getEmail() + " уже существует!");
            }
        }
        User newUser = UserMapper.toUser(userDto);
        newUser.setId(++nextId);
        allUsers.put(newUser.getId(), newUser);
        return UserMapper.toDto(newUser);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        if (!allUsers.containsKey(userId)) {
            throw new IllegalArgumentException("Пользователь с id = " + userId + " не найден!");
        }
        User updatedUser = updateUserFromDtoUser(userId, userDto);
        allUsers.put(updatedUser.getId(), updatedUser);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new IllegalArgumentException("Пользователь с id = " + userId + " не найден!");
        }
        allUsers.remove(userId);
    }

    private User updateUserFromDtoUser(Long userId, UserDto userDto) {
        User updatedUser = allUsers.get(userId);

        if (userDto.getName() != null) {
            updatedUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            for(User u : allUsers.values()) {
                if (u.getEmail().equals(userDto.getEmail()) && (u.getId() != userId)) {
                    throw new IllegalArgumentException("Пользователь с email = " + userDto.getEmail() + " уже существует!");
                }
            }
            updatedUser.setEmail(userDto.getEmail());
        }
        updatedUser.setId(userId);
        return updatedUser;
    }
}

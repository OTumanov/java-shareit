package ru.practicum.shareit.user.storge;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {
    UserDto getUserById(Long id);

    List<UserDto> getAllUsers();

    UserDto createUser(UserDto userDto);

    UserDto updateUser(Long userId, UserDto userDto);

    void deleteUser(Long userId);
}

package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    public UserDto getUserById(Long id);

    public List<UserDto> getAllUsers();

    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(Long UserId, UserDto userDto);

    void deleteUser(Long userId);
}

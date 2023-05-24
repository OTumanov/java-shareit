package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUserById(Long id) {
        return userRepository.getUserById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return userRepository.createUser(userDto);
    }

    @Override
    public UserDto updateUser(Long UserId, UserDto userDto) {
        return userRepository.updateUser(UserId, userDto);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}

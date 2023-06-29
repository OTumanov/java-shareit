package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storge.UserRepository;
import ru.practicum.shareit.user.utils.UserMapper;

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

//    @Override
//    public User updateUser(Long userId, User user) {
//        User updateUser = patchUser(userId, user);
//
//        return userRepository.save(updateUser);
//    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = patchUser(userId, userDto);
        user = userRepository.save(user);
        return UserMapper.toDto(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

//    private User patchUser(Long userId, User user) {
//        User patchedUser = userRepository.getById(userId);
//        String name = user.getName();
//        String newEmail = user.getEmail();
//        String oldEmail = patchedUser.getEmail();
//        if (name != null && !name.isBlank()) {
//            patchedUser.setName(name);
//        }
//        if (!oldEmail.equals(newEmail) && newEmail != null && !newEmail.isBlank()) {
//            patchedUser.setEmail(newEmail);
//        }
//
//        return patchedUser;
//    }

    private User patchUser(Long userId, UserDto patch) {
        User entry = findUserById(userId);
        String name = patch.getName();
        if (name != null && !name.isBlank()) {
            entry.setName(name);
        }

        String oldEmail = entry.getEmail();
        String newEmail = patch.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !oldEmail.equals(newEmail)) {
            entry.setEmail(newEmail);
        }
        return UserMapper.toModel(entry, userId);
    }
}

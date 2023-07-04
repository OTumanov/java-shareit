package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserMapper;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя с id = {}", id);
        return UserMapper.toDto(userService.findUserById(id));
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        return UserMapper.toDtoList(userService.findAllUsers());
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        return UserMapper.toDto(userService.createUser(UserMapper.toUser(userDto)));
    }

    @PatchMapping({"/{userId}"})
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id = {}", userId);
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping({"/{userId}"})
    public void deleteUser(@PathVariable Long userId) {
        log.info("Получен запрос на удаление пользователя с id = {}", userId);
        userService.deleteUserById(userId);
    }
}

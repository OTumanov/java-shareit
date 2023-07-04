package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storge.UserRepository;
import ru.practicum.shareit.user.utils.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        user = new User(1L, "user1", "user1@email.com");
    }

    @Test
    void createUserTest() {
        User savedUser = new User();
        savedUser.setName(user.getName());
        savedUser.setEmail(user.getEmail());
        UserDto savedDto = UserMapper.toDto(savedUser);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        User userDto = userService.createUser(UserMapper.toUser(savedDto));

        assertNotNull(userDto);
        assertEquals(1, userDto.getId());
        assertEquals(savedUser.getName(), userDto.getName());
        assertEquals(savedUser.getEmail(), userDto.getEmail());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUserTest() {
        user.setName("updated name");
        UserDto inputDto = UserMapper.toDto(user);

        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.of(user));

        UserDto userDto = userService.updateUser(1L, inputDto);

        assertNotNull(userDto);
        assertEquals(userDto.getId(), 1);
        assertEquals(userDto.getName(), inputDto.getName());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void findUserByIdTest() {
        when(userRepository.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(user));

        User userDto = userService.findUserById(1L);

        assertNotNull(userDto);
        assertEquals(1, userDto.getId());

        verify(userRepository, times(1)).findById(any(Long.class));
    }

    @Test
    void deleteUserByIdTest() {
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void findAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.singletonList(user));

        List<User> dtos = userService.findAllUsers();

        assertNotNull(dtos);
        assertEquals(1, dtos.size());
        assertEquals(user.getId(), dtos.get(0).getId());

        verify(userRepository, times(1)).findAll();
    }
}
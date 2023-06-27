package ru.practicum.shareit.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storge.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;

    private final User user1 = new User(1L, "testUser", "test@email.com");
    private final User user2 = new User(2L, "testUser2", "test2@email.com");

    @Test
    public void createUserTest() {
        when(userRepository.save(any())).thenReturn(user1);

        assertThat(userService.createUser(user1), equalTo(user1));
    }

    @Test
    public void getUserByIdExistTest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user1));

        assertThat(user1, equalTo(userService.findUserById(1L)));
    }

    @Test
    public void getUserByIdNotExistTest() {
        when(userRepository.findById(anyLong())).thenReturn(empty());

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
        assertThat(exception.getMessage(), equalTo("Нет такого пользователя"));
    }

    @Test
    public void getAllUsersExistTest() {
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<User> users = userService.findAllUsers();
        assertThat(users, equalTo(List.of(user1, user2)));
    }

    @Test
    public void getAllUsersNotExistTest() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        assertThat(userService.findAllUsers(), equalTo(new ArrayList<>()));
    }


    @Test
    public void editUserThrowNotFoundExceptionWhenUserNotExists() {
        when(userRepository.findById(anyLong())).thenReturn(empty());

        Exception exception = Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
        assertThat(exception.getMessage(), equalTo(("Нет такого пользователя")));
    }

    @Test
    public void deleteUser_shouldDeleteUser() {
        userService.deleteUserById(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}
package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exceptions.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.utils.UserMapper;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceImplIntegrationTest {

    private final EntityManager entityManager;
    private final UserService userService;

    @Test
    void createUser() {
        User user1 = new User(null, "testUser", "test@email.com");
        userService.createUser(user1);

        TypedQuery<User> query = entityManager
                .createQuery("Select u from User u where u.email = :email", User.class);
        User user2 = query
                .setParameter("email", user1.getEmail())
                .getSingleResult();

        assertThat(user1.getId(), equalTo(1L));
        assertThat(user1.getName(), equalTo(user2.getName()));
        assertThat(user1.getEmail(), equalTo(user2.getEmail()));
    }

    @Test
    void getUserById() {
        User user1 = new User(null, "testUser", "test@email.com");
        userService.createUser(user1);

        User user2 = userService.findUserById(1L);

        assertThat(user1.getId(), equalTo(1L));
        assertThat(user1.getName(), equalTo(user2.getName()));
        assertThat(user1.getEmail(), equalTo(user2.getEmail()));
    }

    @Test
    void getAllUsers() {
        User user1 = new User(1L, "testUser", "test@email.com");
        User user2 = new User(2L, "test1User", "test1@email.com");

        userService.createUser(user1);
        userService.createUser(user2);

        var usersList = userService.findAllUsers();

        assertThat(usersList, hasSize(2));
        assertThat(usersList.get(0), equalTo(user1));
        assertThat(usersList.get(1), equalTo(user2));
    }

    @Test
    void editUser() {
        User user1 = new User(1L, "testUser", "test@email.com");

        User userUpdate1 = new User(1L, "testUserUpdate", "testUserUpdate@email.com");
        User userUpdate2 = new User(1L, "testUserUpdate", "testUserUpdate2@email.com");
        User userUpdate3 = new User(1L, "testUserUpdate3", "testUserUpdate@email.com");

        userService.createUser(user1);

        UserDto updatedUser = userService.updateUser(1L, UserMapper.toDto(userUpdate1));

        assertThat(updatedUser.getName(), equalTo(userUpdate1.getName()));
        assertThat(updatedUser.getEmail(), equalTo(userUpdate1.getEmail()));

        updatedUser = userService.updateUser(1L, UserMapper.toDto(userUpdate2));

        assertThat(updatedUser.getEmail(), equalTo(userUpdate2.getEmail()));

        updatedUser = userService.updateUser(1L, UserMapper.toDto(userUpdate3));

        assertThat(updatedUser.getName(), equalTo(userUpdate3.getName()));
    }

    @Test
    void deleteUser() {
        User user1 = new User(1L, "testUser", "test@email.com");
        userService.createUser(user1);

        userService.deleteUserById(1L);

        Exception e = Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(1L));
        assertThat(e.getMessage(), equalTo("Нет такого пользователя"));
    }
}
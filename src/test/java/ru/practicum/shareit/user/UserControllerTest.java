package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;

    private static final String BASE_PATH_USERS = "/users";

    User user = User.builder()
            .id(1L)
            .name("testUser")
            .email("test@email.com")
            .build();

    @Test
    void addUserTest() throws Exception {
        when(userService.createUser(any())).thenReturn(user);

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.findUserById(any())).thenReturn(user);

        mvc.perform(get(BASE_PATH_USERS + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(user));

        mvc.perform(get(BASE_PATH_USERS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(user.getName())))
                .andExpect(jsonPath("$.[0].email", is(user.getEmail())));
    }

    @Test
    public void updateUserTest() throws Exception {
        long userId = 1L;
        UserDto userDto = createTestUserDto(userId);
        userDto.setName("updatedName");

        when(userService.updateUser(any(Long.class), any(UserDto.class)))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, times(1)).updateUser(any(Long.class), any(UserDto.class));
    }

    @Test
    void deleteUserTest() throws Exception {
        mvc.perform(delete(BASE_PATH_USERS + "/1")).andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(anyLong());
    }

    @Test
    void addInvalidUserTest() throws Exception {
        User emptyNameUser = new User(1L, "", "test@email.com");
        User invalidEmailUser = new User(1L, "testUser", "testemail.com");
        User emptyEmailUser = new User(1L, "testUser", "");

        when(userService.createUser(any())).thenReturn(user);

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(emptyNameUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(invalidEmailUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(post(BASE_PATH_USERS)
                        .content(mapper.writeValueAsString(emptyEmailUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findAllUsersTest() throws Exception {
        when(userService.findAllUsers()).thenReturn(Collections.emptyList());

        mvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService, times(1)).findAllUsers();
    }


    @Test
    public void deleteUserByIdTest() throws Exception {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUserById(any(Long.class));
    }

    private UserDto createTestUserDto(Long id) {
        String name = "user";
        String email = "user@user.com";

        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        return dto;
    }
}
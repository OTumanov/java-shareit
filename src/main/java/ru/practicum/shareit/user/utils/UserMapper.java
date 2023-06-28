package ru.practicum.shareit.user.utils;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class UserMapper {
    public static UserDto toDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserDto userDto) {

        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public static List<UserDto> toDtoList(List<User> allUsers) {
        List<UserDto> result = new ArrayList<>();
        for (User u : allUsers) {
            result.add(toDto(u));
        }

        return result;
    }

    public static List<User> toUserList(List<UserDto> allUsers) {
        List<User> result = new ArrayList<>();
        for (UserDto u : allUsers) {
            result.add(toUser(u));
        }

        return result;
    }

    public static User toModel(UserDto userDto, Long userId) {
        return new User(userId, userDto.getName(), userDto.getEmail());
    }

    public static List<UserDto> mapUserListToUserDtoList(List<User> users) {
        List<UserDto> result = new ArrayList<>();
        for (User user : users) {
            result.add(toDto(user));
        }
        return result;
    }
}

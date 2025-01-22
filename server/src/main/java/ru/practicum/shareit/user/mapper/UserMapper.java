package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

@Mapper
public interface UserMapper {

    UserDto toUserDto(User user);

    UserDtoShort toUserDtoShort(User user);

    User toUser(UserDto userDto);
}


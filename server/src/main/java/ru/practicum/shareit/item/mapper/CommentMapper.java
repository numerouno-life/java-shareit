package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(imports = java.time.LocalDateTime.class)
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorName", source = "author.name")
    CommentDtoOut toCommentDtoOut(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "author")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "text", source = "commentDtoIn.text")
    @Mapping(target = "created", expression = "java(LocalDateTime.now())")
    Comment toComment(CommentDtoIn commentDtoIn, Item item, User author);
}

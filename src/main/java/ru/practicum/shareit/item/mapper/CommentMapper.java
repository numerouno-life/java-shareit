package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDtoIn;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public CommentDtoOut toCommentDtoOut(Comment comment) {
        return CommentDtoOut.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated()).build();
    }

    public Comment toComment(CommentDtoIn commentDtoIn, Item item, User author) {
        return Comment.builder()
                .item(item)
                .author(author)
                .text(commentDtoIn.getText())
                .created(LocalDateTime.now()).build();
    }
}
package com.pttbackend.pttclone.mapper;

import java.util.List;

import com.pttbackend.pttclone.dto.CommentDTO;
import com.pttbackend.pttclone.dto.CommentRequest;
import com.pttbackend.pttclone.model.Comment;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


import static java.util.stream.Collectors.toList;



/**
 * Comment Mapper
 */
@Mapper(componentModel = "spring" )
public interface CommentMapper {
    
    /**
     * {@link com.pttbackend.pttclone.model.Comment} maps to 
     * {@link com.pttbackend.pttclone.dto.CommentDTO}
     * @param comment {@link Comment}
     * @return {@link CommentDTO}
     */
    @Mapping(target="commentId", source = "id")
    @Mapping(target ="postId" , expression = "java(comment.getPost().getId())")
    @Mapping(target ="username", expression = "java(comment.getUser().getUsername())")
    @Mapping(target ="duration", expression = "java(getDuration(comment))")
    @Mapping(target = "text", source="text")
    @Mapping(target = "childComments", expression = "java( comment.getChildComments() != null ? (mapToListCommentDTO(comment)) : null)")
    CommentDTO mapToCommentDTO(Comment comment);

    /**
     * {@link CommentDTO}, {@link Post} and {@link User}
     * map to {@link CommentDTO} 
     * @param commentDTO {@link CommentDTO}
     * @param post {@link Post}
     * @param user {@link User}
     * @return {@link Comment}
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentDTO.text")
    @Mapping(target = "duration", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    Comment mapToComment(CommentDTO commentDTO,Post post,User user);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "commentReq.text")
    @Mapping(target = "duration", expression = "java(java.time.Instant.now())")
    @Mapping(target = "post", source = "post")
    @Mapping(target = "user", source = "user")
    Comment commentRequestMapToComment(CommentRequest commentReq, Post post, User user);

    default String getDuration(Comment comment){
        return com.github.marlonlom.utilities.timeago.TimeAgo.using(comment.getDuration().toEpochMilli() );
    }

    default List<CommentDTO> mapToListCommentDTO(Comment comment){
        return comment.getChildComments().stream().map(this::mapToCommentDTO).collect(toList());
    }
}
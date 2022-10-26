package com.pttbackend.pttclone.dto;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@ApiModel("Comment DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    @ApiModelProperty(value = "Comment ID")
    private Long commentId ;
    @ApiModelProperty(value = "Post ID")
    private Long postId; 
    @ApiModelProperty(value = "Comment's Text")
    private String text;
    @ApiModelProperty(value = "Username", name = "the person who made comment")
    private String username;
    @ApiModelProperty(value = "duration")
    private String duration;
    @ApiModelProperty(value = "replied", name = "this comment is replied to which comment")
    private Long repliedTo;
    @ApiModelProperty(value = "Child Comments")
    private List<CommentDTO> childComments;
}

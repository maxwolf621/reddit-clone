package com.pttbackend.pttclone.dto;

import java.util.List;

import com.pttbackend.pttclone.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long postId; 
    private String text;
    private String username;
    private String duration;
    private List<Comment> comments;
}

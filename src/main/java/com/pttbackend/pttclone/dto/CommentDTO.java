package com.pttbackend.pttclone.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long commentId ;
    private Long postId; 
    private String text;
    private String username;
    private String duration;
    private Long repliedTo;
    private List<CommentDTO> childComments;
}

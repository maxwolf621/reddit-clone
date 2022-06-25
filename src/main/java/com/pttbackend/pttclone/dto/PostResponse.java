package com.pttbackend.pttclone.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {
    private Long id;
    private String postname;
    private String url;
    private String description;
    private String username;
    private String subname;
    private String duration;
    private Integer commentCount;
    private Integer voteCount;
    
    private boolean upVote;
    private boolean downVote; 
    private boolean marked; 
    private Set<String> tagnames;
}

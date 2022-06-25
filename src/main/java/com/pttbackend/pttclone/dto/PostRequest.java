package com.pttbackend.pttclone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private Long id;    
    private String postname;
    private String url;
    private String description;
    private String subname;
}



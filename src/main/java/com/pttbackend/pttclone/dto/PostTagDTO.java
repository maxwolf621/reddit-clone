package com.pttbackend.pttclone.dto;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostTagDTO {
    private PostRequest postRequest;
    private Set<String> tagNames;
}

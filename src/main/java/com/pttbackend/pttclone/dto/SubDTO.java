package com.pttbackend.pttclone.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubDTO {
    private Long id;
    private String subname;
    private String description;
    private Integer postsCount;
}

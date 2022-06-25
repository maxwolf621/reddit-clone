package com.pttbackend.pttclone.dto;

import com.pttbackend.pttclone.model.VoteType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p> User that make vote for a post </p>
 * <p> This Dto contains </p>
 * {@code #postId}
 * {@code #voteType}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteDTO {
    private VoteType voteType;
    private Long postId;
}


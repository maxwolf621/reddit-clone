package com.pttbackend.pttclone.controller;
import com.pttbackend.pttclone.dto.VoteDTO;
import com.pttbackend.pttclone.service.VoteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for voting Post 
 * (Like or DisLike)
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/vote")
@Slf4j
public class VoteController {
    private final VoteService voteservice;

    @PostMapping
    public ResponseEntity<Void> voteForPost(@RequestBody VoteDTO voteDto) {
        log.info("VOTING..........");
        log.info("Post :" + voteDto.getPostId());
        log.info("GET VOTE VALUE"+voteDto.getVoteType().getValue());

        voteservice.voteForPost(voteDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
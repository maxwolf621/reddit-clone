package com.pttbackend.pttclone.controller;
import com.pttbackend.pttclone.dto.VoteDTO;
import com.pttbackend.pttclone.service.VoteService;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * Controller for voting Post 
 * (Like or DisLike)
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/vote")
public class VoteController {
    private final VoteService voteService;

    @Operation(summary = "UP-VOTE OR DOWN-VOTE THE POST" )
    @PostMapping
    public ResponseEntity<Void> voteForPost(@RequestBody VoteDTO voteDto) {
        voteService.voteForPost(voteDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
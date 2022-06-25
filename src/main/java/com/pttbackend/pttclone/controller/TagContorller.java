package com.pttbackend.pttclone.controller;

import java.util.List;

import com.pttbackend.pttclone.service.TagService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/tag")
@AllArgsConstructor
public class TagContorller {
    private final TagService tagService;

    @GetMapping("/getalltags")
    public ResponseEntity<List<String>> getAllTags(){
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAlltags());    
    }
}

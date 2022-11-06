package com.pttbackend.pttclone.controller;

import java.util.Set;

import com.pttbackend.pttclone.service.TagService;

// import io.swagger.v3.oas.annotations.Operation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("api/tag")
@AllArgsConstructor
public class TagController {
    private final TagService tagService;

    // @Operation(summary = "GET ALL TAGS")
    @GetMapping("/getalltags")
    public ResponseEntity<Set<String>> getAllTags(){
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAlltags());    
    }
}

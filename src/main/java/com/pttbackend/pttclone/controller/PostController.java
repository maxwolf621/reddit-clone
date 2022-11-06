package com.pttbackend.pttclone.controller;

import java.util.List;

import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.PostTagDTO;
import com.pttbackend.pttclone.service.PostService;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * <p> Controller For Post to </p>
 * <p> Get All Posts</p>
 * <p> Get Specific Post</p>
 * <p> Get Post By Sub</p>
 * <p> Get Post By User Name</p>
 * <p> Create Post </p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }

    @GetMapping("getByPost/{postId}")
    public ResponseEntity<PostResponse> getPostById(
        @PathVariable long postId){
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }

    @GetMapping("getBySub/{subId}")
    public ResponseEntity<List<PostResponse>> getPostsBySub(
        @PathVariable Long subId) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsBySubId(subId));
    }

    @GetMapping("getByUser/{userName}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(
        @PathVariable String userName) {
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByUsername(userName));
    }

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostTagDTO postTagDTO) {
        postService.save(postTagDTO);        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePostById(Long postId){
        postService.deletePostById(postId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

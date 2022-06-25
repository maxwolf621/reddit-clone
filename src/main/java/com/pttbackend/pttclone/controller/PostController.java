package com.pttbackend.pttclone.controller;

import java.util.List;

import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.PostTagDTO;
import com.pttbackend.pttclone.service.PostService;

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
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class PostController {
    private final PostService postService;
    
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(){
        log.info("** Get all posts");
        return ResponseEntity.status(HttpStatus.OK).body(postService.getAllPosts());
    }
    @GetMapping("getByPost/{postId}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable long postId){
        log.info("** Get post by post id");
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPost(postId));
    }
    @GetMapping("getBySub/{subId}")
    public ResponseEntity<List<PostResponse>> getPostsBySub(@PathVariable Long subId) {
        log.info("** Get posts in the Sub");
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsBySubId(subId));
    }

    @GetMapping("getByUser/{userName}")
    public ResponseEntity<List<PostResponse>> getPostsByUsername(@PathVariable String userName) {
        log.info("** Get posts of username "+ userName);
        return ResponseEntity.status(HttpStatus.OK).body(postService.getPostsByUsername(userName));
    }

    @PostMapping
    public ResponseEntity<Void> createPost(@RequestBody PostTagDTO postTagDTO) {
        log.info("** Saving the post and tags via postService ");

        log.info("post request" + postTagDTO.getPostRequest().toString());
        log.info("tag names " + postTagDTO.getTagNames().toString());
        postService.save(postTagDTO);
        
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletePostById(Long postId){
        postService.deletePostById(postId);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}

package com.pttbackend.pttclone.controller;

import com.pttbackend.pttclone.dto.CommentDTO;
import com.pttbackend.pttclone.service.CommentService;

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

import java.util.List;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
public class CommentController {

    private CommentService commentService;
    
    //@Operation(summary="CREATE COMMENT")
    @PostMapping()
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentReq){
        
        return  ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(commentReq));
    }

    //@Operation(summary = "GET COMMENTS OF POST")
    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentByPostId(
        /**@Parameter(description = "Comments of specific Post's ID") **/
        @PathVariable Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByPostId(postId));
    }

    //@Operation(summary = "GET COMMENT FROM THE USER")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentDTO>> getCommentsByUser(
        /**@Parameter(description = "Comments of specific User " )**/
        @PathVariable String username){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByUser(username));        
    }

    //@Operation(summary = "DELETE COMMENT")
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> deleteByCommentId(
        /** @Parameter(description = "Delete Comment using its ID")**/
        @PathVariable long commentId){
        Long result= commentService.deleteByCommentId(commentId);
        
        if(result == 0){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Comment Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Comment is removed Successfully");        
    }

}

package com.pttbackend.pttclone.controller;

import com.pttbackend.pttclone.dto.CommentDTO;
import com.pttbackend.pttclone.service.CommentService;

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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/comment")
@AllArgsConstructor
@Slf4j
public class CommentController {
    private CommentService commentService;
    
    @PostMapping()
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentDTO commentReq){
        
        log.info("Create A Comment\n" +
                 "parent comment id : " + commentReq.getRepliedTo() +
                 "\nComment Text : "+ commentReq.getText() +
                 "\nPost id : " + commentReq.getPostId()
                 );

        return  ResponseEntity.status(HttpStatus.CREATED).body(commentService.save(commentReq));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<CommentDTO>> getCommentByPostId(@PathVariable Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByPostId(postId));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<CommentDTO>> getCommentsByUser(@PathVariable String username){
        return ResponseEntity.status(HttpStatus.OK).body(commentService.getCommentsByUser(username));        
    }

    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<String> getCommentsByUser(@PathVariable long commentId){
        Long result= commentService.deleteByCommentId(commentId);
        
        if(result == 0){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Comment Not Found");
        }
        return ResponseEntity.status(HttpStatus.OK).body("Comment is removed Successfully");        
    }

}

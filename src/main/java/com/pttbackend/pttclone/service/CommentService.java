package com.pttbackend.pttclone.service;

import java.util.List;

import static java.util.stream.Collectors.toList;

import com.pttbackend.pttclone.dto.CommentDTO;
import com.pttbackend.pttclone.dto.CommentRequest;
import com.pttbackend.pttclone.mapper.CommentMapper;
import com.pttbackend.pttclone.model.Comment;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.CommentRepository;
import com.pttbackend.pttclone.repository.PostRepository;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p> A comment Service to </p>
 * <p> get the comments and create a comment </p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class CommentService {
    
    private final CommentRepository commentRepo;     
    private final PostRepository postRepo;           
    private final UserRepository userRepo;          
    private final AuthenticationService authService; 
    
    private final CommentMapper commentMapper;       

    /**
     * <p> Get All the comment for that post </p>
     * <pre> @Transactional (readOnly = true) </pre>
     * @param postId {@link Post}'s id
     * @return {@code List<CommentDTO>}
     */
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByPostId(Long postId){
        Post post = postRepo.findById(postId).orElseThrow(()->new RuntimeException("Post " +postId.toString() + "Not Found"));
        List<Comment> comments = commentRepo.findByPost(post);

        log.info("return all comments in " + post.getPostname());

        return comments.stream().filter(comment ->comment.getRootComment() == null).map(commentMapper::mapToCommentDTO).collect(toList());
    }

    /**
     * <p> Get Comments For That User </p> 
     * <p> make a transaction with {@code @Transactional(readOnly = true)} </p>
     * @param username {@code AuthenticationService#CurrentUser()}
     * @return {@code List<CommentDTO>}
     */
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByUser(String username){
        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
        List<Comment> comments = commentRepo.findAllByUser(user);
        
        log.info("return all comments from" + user.getUsername());

        return comments.stream().map(commentMapper::mapToCommentDTO).collect(toList());
    }


    /**
     * Leave a comment in a post
     * @param commentReq {@link CommentDTO} 
     * @return commentDTO
     */
    public CommentDTO save(CommentDTO commentReq){
        
        Post post = postRepo.findById(commentReq.getPostId()).orElseThrow(() -> new RuntimeException("Post Not Found"));
        
        Comment comment = commentMapper.mapToComment(commentReq ,post, authService.getCurrentUser());
        
        if(commentReq.getRepliedTo() != null){
            commentRepo.findById(commentReq.getRepliedTo()).ifPresent
            (    
                comment::setRootComment
            );
        }
        
        return commentMapper.mapToCommentDTO(commentRepo.save(comment));

    }
    /**
     * 
     * @param id {@link Comment}'s id
     * @return {@code Long} if returned value is less equal than 0 then query of deletion is failed
     */
    @Transactional
    public Long deleteByCommentId(Long id){       
        User user = authService.getCurrentUser();
        long result = commentRepo.deleteByUserAndId(user, id);

        log.info("query result " + result);
        return result;
    }

}   

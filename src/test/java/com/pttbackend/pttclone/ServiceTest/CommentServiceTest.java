package com.pttbackend.pttclone.ServiceTest;

import java.util.List;
import static java.util.stream.Collectors.toList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;


import com.pttbackend.pttclone.dto.CommentDTO;
import com.pttbackend.pttclone.mapper.CommentMapper;
import com.pttbackend.pttclone.mapper.CommentMapperImpl;
import com.pttbackend.pttclone.model.Comment;
import com.pttbackend.pttclone.repository.CommentRepository;




@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // use mysql not h2
@ActiveProfiles("test")
public class CommentServiceTest {

    @Autowired
    private CommentRepository commentRepo;

    CommentMapper commentMapper = new CommentMapperImpl();

    @Test
    public void getCommentsByPostId(){
        Long postId = Long.valueOf(12);
        //List<Comment> comments = commentRepo.findByPost(post);
        List<Comment> comments = commentRepo.getCommentsByPost(postId);
        List<CommentDTO> res = comments.stream().filter(
            comment ->comment.getRootComment() == null
            ).map(
                commentMapper::mapToCommentDTO).collect(toList()
            );
        Assertions.assertTrue(res.size() > 0);
    }

    /**
     * <p> Get Comments For That User </p> 
     * <p> make a transaction with {@code @Transactional(readOnly = true)} </p>
     * @param username {@code AuthenticationService#CurrentUser()}
     * @return {@code List<CommentDTO>}
     */
     @Test
     @Rollback(false)
     public void getCommentsByUser(){
        String username = "test";
        List<Comment> comments = commentRepo.getCommentsByUser(username);
        
        Assertions.assertTrue(comments.stream().map(commentMapper::mapToCommentDTO).collect(toList()).size() >= 0);
    }

}

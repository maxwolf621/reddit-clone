package com.pttbackend.pttclone.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.repository.PostRepository;

import lombok.extern.slf4j.Slf4j;

//@RunWith(SpringRunner.class) already in DataJpaTest 
//@TestPropertySource("classpath:application-test.properties")
//@ContextConfiguration(locations = "application-test.properties")
@DataJpaTest 
@AutoConfigureTestDatabase(replace = Replace.NONE) // use mysql not h2
@ActiveProfiles("test")
@Slf4j
public class PostServiceTest {

    @Autowired
    private PostRepository postRepo;

    @Test
    @DisplayName("Test Get All Posts")
    @Rollback(false)
    public void getAllPosts(){
        List<Post> e = postRepo.findAll();
        e.stream().forEach(
            p -> log.info(p.getPostname())
        );
        Assertions.assertTrue(e.size() > 0);
    } 

    @Test
    @Rollback(false)
    public void getPostByName(){
        long l = 87;
        Post p = postRepo.getPostById(l).get();
        assertNotNull(p);
    }

    @Test
    @Rollback(false)
    public void getPostBySubId(){
        List<Post> ps = postRepo.getPostBySubId(1);
        Assertions.assertTrue(ps.size() > 0);
    }

    @AfterAll
    static void finish(){
        log.info("Execute All test methods");
    }
}

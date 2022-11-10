package com.pttbackend.pttclone.service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.sql.SQLIntegrityConstraintViolationException;

import com.pttbackend.pttclone.config.threadpool.AsyncConfiguration;
import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.PostTagDTO;
import com.pttbackend.pttclone.mapper.PostMapper;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.Tag;
import com.pttbackend.pttclone.repository.PostRepository;
import com.pttbackend.pttclone.repository.SubRepository;
import com.pttbackend.pttclone.repository.TagRepository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor 
@CacheConfig(cacheNames = "post")
public class PostService {

    private final AuthenticationService authService;
    private final PostMapper postMapper;

    private final PostRepository postRepo;
    private final SubRepository subRepo;
    private final TagRepository tagRepo;

    /**
     * Show all the posts
     * @return {@code List<PostResponse>}
     */
    @Async(AsyncConfiguration.TASK_EXECUTOR_SERVICE)
    @Transactional(readOnly = true)
    public CompletableFuture<List<PostResponse>> getAllPosts(){
       return postRepo.findAllPosts().thenApply(
        posts -> posts.stream().map(postMapper::mapToPostResponse).collect(toList())
       );
    }

    /**
     * search post by post's id
     * @param postId {@link Post}'s Id
     * @return {@code PostResponse}
     */
    @Transactional(readOnly = true)
    public PostResponse getPost(long postId){
        Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post Not Found"));

        return this.postMapper.mapToPostResponse(post);
    }

    /** 
     * Save the post in the sub
     * @param postTagDTO {@link PostTagDTO}
     * @see <a href="https://stackoverflow.com/questions/27849968/transactional-norollbackfor-runtimeexception-class-does-not-prevent-rollback">no-Roll-back-for</a>
     * @see <a href="https://stackoverflow.com/questions/175186/updating-an-object-within-a-set">Set and Duplicate Object</a>
     */ 
    @Transactional(noRollbackFor = SQLIntegrityConstraintViolationException.class)
    public void save(PostTagDTO postTagDTO) {
        
        Sub sub = subRepo.findBySubname(postTagDTO.getPostRequest().getSubname()).orElseThrow(() -> new RuntimeException("Post Can Not Be Created"));
    
        // Map PostDTO.tags to Tag type
        Set<Tag> tags = postTagDTO.getTagNames().stream().map(postMapper::mapToTag).collect(toSet());

        // Duplicates 
        postTagDTO.getTagNames().stream().forEach(
            tagname ->{
                tagRepo.findByTagname(tagname).ifPresent(
                    tag -> {
                        // remove the tag that already existed in database
                        // comparing with tagname only (ignore id)
                        tags.remove(tag);
                        // add the Existing tag
                        tags.add(tag);
                    }
                );
            }
        );

        postRepo.save(postMapper.mapToPost(
            postTagDTO.getPostRequest(), 
            tags, 
            sub,
            authService.getCurrentUser()));
    }

    /**
     * Search the certain post by {@link Sub}'s Id
     * @param subId {@link Sub}'s Id
     * @return {@code List<PostResponse>}
     */
    @Transactional(readOnly = true)
    //@Cacheable(key = "#subId")
    public List<PostResponse> getPostsBySubId(Long subId){

        subRepo.findById(subId).orElseThrow(() ->new RuntimeException("Sub Not Found"));    
        
        List<Post> posts = postRepo.getPostBySubId(subId);

        return posts.stream() 
                    .map(postMapper::mapToPostResponse)
                    .collect(toList());
    }


    public void deletePostById(Long id){
        postRepo.deleteById(id);
    }

    @Async(AsyncConfiguration.TASK_EXECUTOR_SERVICE)
    @org.springframework.transaction.annotation.Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<List<PostResponse>> findByUser(String username) {
        return postRepo.findPostsByUserName(username).thenApply(
            posts -> posts.stream().map(postMapper::mapToPostResponse).collect(toList())
        );
    }
}
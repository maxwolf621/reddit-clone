package com.pttbackend.pttclone.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.sql.SQLIntegrityConstraintViolationException;

import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.PostTagDTO;
import com.pttbackend.pttclone.mapper.PostMapper;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.Tag;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.PostRepository;
import com.pttbackend.pttclone.repository.SubRepository;
import com.pttbackend.pttclone.repository.TagRepository;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor 
@Slf4j
@CacheConfig(cacheNames = "post")
public class PostService {
    private final AuthenticationService authService;
    private final PostRepository postRepo;
    private final SubRepository subRepo;
    private final UserRepository userRepo;
    private final PostMapper postMapper;
    private final TagRepository tagRepo;

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, PostResponse> redisTemplatePostResponse;

    /**
     * Show all the posts
     * @return {@code List<PostResponse>}
     */
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts(){
       return postRepo.findAll().stream()
                      .map(postMapper::mapToPostResponse)
                      .collect(toList());
    }

    /**
     * search post by post's id
     * @param postId {@link Post}'s Id
     * @return {@code PostResponse}
     */
    @Transactional(readOnly = true)
    public PostResponse getPost(long postId){

        String key = Long.toString(postId);

        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            log.info("Get post : "+ postId +" from redis");
            return (PostResponse) redisTemplate.opsForValue().get(key);
        }

        Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post Not Found"));
        PostResponse postResponse = this.postMapper.mapToPostResponse(post);
        redisTemplate.opsForValue().set(key, postResponse);
        return postResponse;
    }

    /**
     * Save the post in the sub
     * @param postTagDTO {@link PostTagDTO}
     * @see <a href="https://stackoverflow.com/questions/27849968/transactional-norollbackfor-runtimeexception-class-does-not-prevent-rollback">no-Roll-back-for</a>
     * @see <a href="https://stackoverflow.com/questions/175186/updating-an-object-within-a-set">Set and Duplicate Object</a>
     */ 
    @Transactional(noRollbackFor = SQLIntegrityConstraintViolationException.class)
    public void save(PostTagDTO postTagDTO) {
        
        Sub sub = subRepo.findBySubname(postTagDTO.getPostRequest().getSubname()).orElseThrow(() -> new RuntimeException("Post Can Not Create"));
        
        Set<Tag> tags = postTagDTO.getTagNames().stream().map(postMapper::mapToTag).collect(toSet());
  
        // Check if duplicates exist
        postTagDTO.getTagNames().stream().forEach(
            tagname ->{
                tagRepo.findByTagname(tagname).ifPresentOrElse(
                    tag -> {
                        log.info("tag id" + tag.getId());
                        tags.remove(tag);
                        tags.add(tag);
                    } ,()-> log.info("Continue...")
            );
        });

        postRepo.save(postMapper.mapToPost(postTagDTO.getPostRequest(), tags, sub, authService.getCurrentUser()));
              
        /*
        tags.stream().forEach(
            tag -> log.info("id "+ tag.getId())
        );
        log.info("post tags"+post.getTags());
        */

        log.info("** Saving post successfully");
    }

    /**
     * Search the certain post by {@link Sub}'s Id
     * @param subId {@link Sub}'s Id
     * @return {@code List<PostResponse>}
     */
    @Transactional(readOnly = true)
    @Cacheable(key = "#subId")
    public List<PostResponse> getPostsBySubId(Long subId){

        Sub sub = subRepo.findById(subId).orElseThrow(() ->new RuntimeException("Sub Not Found"));    
        List<Post> posts = postRepo.findAllBySub(sub);

        return posts.stream() 
                    .map(postMapper::mapToPostResponse)
                    .collect(toList());
    }

    /**
     * Search certain post by {@link User}'s name
     * @param username {@link User}'s name
     * @return {@code List<PostResponse>}
     */
    @Transactional(readOnly = true)
    //@SuppressWarnings("unchecked")
    public List<PostResponse> getPostsByUsername(String username){
        
        if(Boolean.TRUE.equals(redisTemplate.hasKey(username))){
            log.info("Get From Redis");
            //return redisTemplatePostResponse.opsForList().range(username, 0, -1);
            var caches = redisTemplate.opsForList().range(username, 0, -1);

            if(caches.isEmpty()){
                return Collections.emptyList();
            }
            else
                return caches.stream().filter(PostResponse.class::isInstance).map(PostResponse.class::cast).collect(toList());
        }

        User user = userRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));  
        List<Post> posts = postRepo.findByUser(user);
        List<PostResponse> postResponses = posts.stream().map(postMapper::mapToPostResponse).collect(toList());
        //redisTemplatePostResponse.opsForList().rightPushAll(username, postResponses);
        redisTemplate.opsForList().rightPushAll(username, postResponses);
        return postResponses; 
    }

    
    public void deletePostById(Long id){
        postRepo.deleteById(id);
    }
}
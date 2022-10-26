package com.pttbackend.pttclone.service;

import java.util.Optional;

import com.pttbackend.pttclone.dto.VoteDTO;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.model.Vote;
import com.pttbackend.pttclone.model.VoteType;

import com.pttbackend.pttclone.repository.PostRepository;
import com.pttbackend.pttclone.repository.VoteRepository;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
/**
 * The Vote For The Post
 */
@Service
@AllArgsConstructor
@Slf4j
public class VoteService {
    private final VoteRepository voteRepo;
    private final PostRepository postRepo;
    private final AuthenticationService authService;
    private final RedisTemplate<String, Object> redisTemplate;

    /** 
     * {@code User} votes for a certain {@code Post}
     * @param votedto {@link VoteDTO}
     */
    public void voteForPost(VoteDTO voteDTO){
        
        User currentUser = authService.getCurrentUser();
        Post currentPost = postRepo.findById(voteDTO.getPostId()).orElseThrow(() -> new RuntimeException("Post Not Found"));

        Optional<Vote> vote = voteRepo.findTopByPostAndUserOrderByVoteIdDesc(currentPost, currentUser);
    
        HashOperations<String,String,Object> hashOPS = redisTemplate.opsForHash();
        
        /** 
        if(Boolean.TRUE.equals(hashOPS.hasKey(voteDTO.getPostId().toString(), currentUser.getUsername()))){

        }else{

        }
        */
        
        //check if the user voted  
        if(vote.isPresent()){
            log.info("** The User have already voted...");
            // if previous vote is downvote (-1) then current count - (-1)
            // if previous vote is upvote (+1) then current count - (+1)
            currentPost.setVoteCount(currentPost.getVoteCount() - vote.get().getVoteType().getValue());
            voteRepo.delete(vote.get());
            if(!vote.get().getVoteType().equals(voteDTO.getVoteType())){
               // vote exists and has the different voteType with previous oneP
               currentPost.setVoteCount(currentPost.getVoteCount() + voteDTO.getVoteType().getValue());
               voteRepo.save(mapToVote(voteDTO, currentPost));
            }
        }
        else{
            Optional.of(voteDTO.getVoteType()).filter(m -> m.equals(VoteType.UPVOTE)).ifPresentOrElse(
                upVote ->{
                    log.info("********************************** Up-voting this post");
                    currentPost.setVoteCount(currentPost.getVoteCount() + 1);
                }, () -> {
                    log.info("********************************* Down-voting this post");
                    currentPost.setVoteCount(currentPost.getVoteCount() - 1);
                }
            );
            voteRepo.save(mapToVote(voteDTO, currentPost));
        }
        postRepo.save(currentPost);
    }
    
    /**
     *  
     * {@code voteDTO} and {@code Post} map to {@code Vote}
     * @param voteDTO {@link VoteDTO}
     * @param post {@link Post}
     * @return {@link Vote} 
     */
    private Vote mapToVote(VoteDTO voteDTO, Post post) {
        return Vote.builder()
                .voteType(voteDTO.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}

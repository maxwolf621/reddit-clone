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
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteService {
    private final VoteRepository voteRepo;
    private final PostRepository postRepo;
    private final AuthenticationService authService;
    private final RedisTemplate<String, Integer> redisTemplate;
    private final RedisTemplate<String, Object> timesAmp;
    private HashOperations<String,String,Integer> hashOps;
    private ValueOperations<String, Integer> valOps;
    /** 
     * {@code User} votes for a certain {@code Post}
     * @param votedto {@link VoteDTO}
     */
    public void voteForPost(VoteDTO voteDTO){
        hashOps = redisTemplate.opsForHash();
        valOps = redisTemplate.opsForValue();
        User currentUser = authService.getCurrentUser();

        String nameId = String.valueOf(currentUser.getUserId());
        String postId = String.valueOf(voteDTO.getPostId());

        valOps.setIfAbsent(postId, 0);
        Integer voteCount = valOps.get(postId);
         // HSETNX 
        if(!Boolean.TRUE.equals(hashOps.hasKey(nameId, postId))){
            hashOps.put(nameId,postId,voteDTO.getVoteType().getValue());
            if(voteDTO.getVoteType().equals(VoteType.DOWNVOTE))valOps.decrement(postId);
            else valOps.increment(postId);
            voteCount = valOps.get(postId);
        }else{
            Integer vote = hashOps.get(nameId, postId); 
            voteCount -= vote;
            hashOps.delete(nameId, postId);
            if(voteDTO.getVoteType().getValue() != vote){
                voteCount += voteDTO.getVoteType().getValue();
                hashOps.put(nameId, postId, voteDTO.getVoteType().getValue());
            }
            valOps.set(postId, voteCount);
        }
    }
}
        /*
        Post currentPost = postRepo.getPostById(Long.valueOf(postId)).orElseThrow(() -> new RuntimeException("Post Not Found"));
        Optional<Vote> vote = voteRepo.findTopByPostAndUserOrderByVoteIdDesc(currentPost, currentUser);
        

        currentPost.setVoteCount(voteCount);
        if(vote.isPresent()){
            log.info("updating");
            if(Boolean.TRUE.equals(hashOps.hasKey(nameId, postId))){
                long i = hashOps.get(nameId,postId) == -1 ? 0 : 1;
                voteRepo.setVoteTypeById(i, vote.get().getVoteId());
            }else{
                log.info("deleting");
                voteRepo.delete(vote.get());
            }
        }else{
            voteRepo.save(mapToVote(voteDTO, currentPost, currentUser));
        }
        postRepo.save(currentPost);
   
        if(vote.isPresent()){
            log.info("** The User have already voted...");
            // if previous vote is downvote (-1) then current count - (-1)
            // if previous vote is upvote (+1) then current count - (+1)
            // currentPost.setVoteCount(currentPost.getVoteCount() - vote.get().getVoteType().getValue());
            //voteRepo.deleteVoteById(vote.get().getVoteId());
            voteRepo.deleteById(vote.get().getVoteId());
            if(!vote.get().getVoteType().equals(voteDTO.getVoteType())){
               // vote exists and has the different voteType with previous one
               currentPost.setVoteCount(currentPost.getVoteCount() + voteDTO.getVoteType().getValue());
               voteRepo.save(mapToVote(voteDTO, currentPost, currentUser));
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
            voteRepo.save(mapToVote(voteDTO, currentPost, currentUser));
        }
        postRepo.save(currentPost);
    }

     *  
     * {@code voteDTO} and {@code Post} map to {@code Vote}
     * @param voteDTO {@link VoteDTO}
     * @param post {@link Post}
     * @return {@link Vote} 

    private Vote mapToVote(VoteDTO voteDTO, Post post, User user) {
        return Vote.builder()
                .voteType(voteDTO.getVoteType())
                .post(post)
                .user(user)
                .build();
    }
*/


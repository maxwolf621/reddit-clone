package com.pttbackend.pttclone.service;

import java.util.Optional;

import com.pttbackend.pttclone.dto.VoteDTO;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.model.Vote;
import com.pttbackend.pttclone.model.VoteType;

import com.pttbackend.pttclone.repository.PostRepository;
import com.pttbackend.pttclone.repository.VoteRepository;

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

    /** 
     * {@code User} votes for a certain {@code Post}
     * @param votedto {@link VoteDTO}
     */
    public void voteForPost(VoteDTO votedto){
        
        User currentUser = authService.getCurrentUser();
        log.info("User :" + currentUser + " are doing voting");

        log.info("find if this post is valid");
        Post currentPost = postRepo.findById(votedto.getPostId()).orElseThrow(() -> new RuntimeException("Post Not Found"));
        
        // To see if the vote for the post by the user exists 
        log.info("find if user have been voted for this post or not");
        Optional<Vote> vote = voteRepo.findTopByPostAndUserOrderByVoteIdDesc(currentPost, currentUser);
        
        //check if the user voted  
        if(vote.isPresent()){// the user already voted for this post 
            log.info("** The User have already voted...");
            log.info("** Current VoteType  :" + votedto.getVoteType());
            log.info("** Previous VoteType :" + vote.get().getVoteType());
            
            // if previous vote is downvote (-1) then current count - (-1)
            // if previous vote is upvote (+1) then current count - (+1)
            currentPost.setVoteCount(currentPost.getVoteCount() - vote.get().getVoteType().getValue());
            voteRepo.delete(vote.get());
            if(!vote.get().getVoteType().equals(votedto.getVoteType())){
               // vote exists and has the different voteType with previous one
               currentPost.setVoteCount(currentPost.getVoteCount() + votedto.getVoteType().getValue());
               voteRepo.save(mapToVote(votedto, currentPost));
            }
        }
        else{
            if(VoteType.UPVOTE.equals(votedto.getVoteType())){
                log.info("** Up-voting this post");
                currentPost.setVoteCount(currentPost.getVoteCount() + 1);
            }else{
                log.info("** Down-voting this post");
                currentPost.setVoteCount(currentPost.getVoteCount() - 1);
            }
            // add new vote row for certain post
            voteRepo.save(mapToVote(votedto, currentPost));
        }
        log.info("** Current Vote Count :"+ currentPost.getVoteCount());
        // update the VoteCount
        postRepo.save(currentPost);
    }
    
    /**
     *  
     * {@code VoteDto} and {@code Post} map to {@code Vote}
     * @param voteDto {@link VoteDTO}
     * @param post {@link Post}
     * @return {@link Vote} 
     */
    private Vote mapToVote(VoteDTO voteDto, Post post) {
        return Vote.builder()
                .voteType(voteDto.getVoteType())
                .post(post)
                .user(authService.getCurrentUser())
                .build();
    }
}

package com.pttbackend.pttclone.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

/**
 * Record vote(Vote Type) for a certain post by a certain user
 * <pre> Vote(Long voteId, VoteType voteType, Post post, User user) </pre>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "vote")
public class Vote {

	@Id
    @GeneratedValue(strategy = AUTO )
    @Column(name="vote_id", unique= true)
    private Long voteId;
    
    @Column(name = "vote_type")
    private VoteType voteType;
    
    /**
     * vote for which post 
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    
    /**
     * who votes
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}

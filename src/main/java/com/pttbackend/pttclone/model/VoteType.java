package com.pttbackend.pttclone.model;

@Deprecated
public enum VoteType {
    // to upvote or downvote for the post
    UPVOTE(1), DOWNVOTE(-1);

    private final int value;
    
    VoteType(int value) {
        this.value = value;
    }
    public int getValue(){
        return this.value;
    }
}

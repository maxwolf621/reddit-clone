package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import com.pttbackend.pttclone.model.Vote;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);
}

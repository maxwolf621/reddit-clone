package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import javax.transaction.Transactional;

import com.pttbackend.pttclone.model.Vote;
import com.pttbackend.pttclone.model.VoteType;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    Optional<Vote> findTopByPostAndUserOrderByVoteIdDesc(Post post, User currentUser);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(value = "UPDATE Vote v SET v.vote_type =?1 where v.vote_id =?2", nativeQuery = true)
    void setVoteTypeById(Long v, Long id);

    @Modifying
    @Transactional
    @Query("Delete From Vote v Where v.id = :id")
    void deleteVoteById(@Param("id") Long id);
}

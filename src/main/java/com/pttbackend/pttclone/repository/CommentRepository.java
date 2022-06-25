package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.pttbackend.pttclone.model.Comment;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    long deleteByUserAndId(User user,Long id);
    List<Comment> findByPost(Post post);
    List<Comment> findAllByUser(User user);
    Optional<Comment> findById(Long id);
}

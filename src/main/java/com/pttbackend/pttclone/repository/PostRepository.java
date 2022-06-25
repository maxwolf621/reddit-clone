package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySub(Sub sub);
    List<Post> findByUser(User user);
}

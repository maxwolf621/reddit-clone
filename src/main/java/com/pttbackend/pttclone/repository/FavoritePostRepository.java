package com.pttbackend.pttclone.repository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.pttbackend.pttclone.model.FavoritePost;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.User;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface FavoritePostRepository extends JpaRepository<FavoritePost, Long> {
    
    List<FavoritePost> findByUser(User user);
    Optional<FavoritePost> findByUserAndFavPost(User user, Post favPost);
    void deleteByUserAndFavPost(User user,Post favPost);
}

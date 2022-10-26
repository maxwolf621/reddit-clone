package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllBySub(Sub sub);
    List<Post> findByUser(User user);

    @Query("Select Distinct p from Post p Left Join Fetch p.tags Left Join Fetch p.votes Where p.id = :id")
    Optional<Post> getPostById(@Param("id") long postId);

    @Query("Select Distinct p from Post p Join Fetch p.tags Where p.sub.id = :id")
    List<Post> getPostBySubId(@Param("id") long subId);

    @Query("Select p from Post p Join Fetch p.tags Join Fetch p.votes where p.user.username = :user")
    List<Post> getPostsByUserName(@Param("user") String user);
}

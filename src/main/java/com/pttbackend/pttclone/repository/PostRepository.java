package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.pttbackend.pttclone.config.threadpool.AsyncConfiguration;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllBySub(Sub sub);

    @Query("Select Distinct p from Post p Left Join Fetch p.tags Where p.id = :id")
    Optional<Post> getPostById(@Param("id") long postId);

    @Query("Select Distinct p from Post p Join Fetch p.tags Where p.sub.id = :id")
    List<Post> getPostBySubId(@Param("id") long subId);

    @Async(AsyncConfiguration.TASK_EXECUTOR_REPOSITORY)
    @EntityGraph(value = "post_sub_user_tags", type= EntityGraph.EntityGraphType.LOAD)
    @Query("Select p from Post p where p.user.username = :user")
    CompletableFuture<List<Post>> findPostsByUserName(@Param("user") String user);

    @Async(AsyncConfiguration.TASK_EXECUTOR_REPOSITORY)
    @EntityGraph(value = "post_sub_user_tags", type= EntityGraph.EntityGraphType.LOAD)
    @Query("Select p from Post p ORDER BY p.duration DESC")
    CompletableFuture<List<Post>> findAllPosts();
}

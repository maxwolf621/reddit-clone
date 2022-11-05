package com.pttbackend.pttclone.service;

import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toList;

import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.mapper.PostMapper;
import com.pttbackend.pttclone.mapper.SubMapper;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.PostRepository;
import com.pttbackend.pttclone.repository.SubRepository;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
@Data
@AllArgsConstructor
public class BookmarkService {

    private final UserRepository userRepo;
    private final SubRepository subRepo;
    private final PostRepository postRepo;
    private final SubMapper subMapper;
    private final PostMapper postMapper;
    private final AuthenticationService authService;

    @Transactional(readOnly = true)
    public List<PostResponse> getMyFavoritePosts(User user){
        return user.getFavPosts().stream().map(postMapper::mapToPostResponse).collect(toList());
    }

    @Transactional(readOnly = true)
    public Set<SubDTO> getMyFavoriteSubs(User user){        
        return user.getFavSubs().stream().map(subMapper::mapToSubDTO).collect(toSet());
    } 

    public void markSubAsMyFav(String subname){
        Sub sub = subRepo.findBySubname(subname).orElseThrow(()-> new RuntimeException("Sub not Found"));
        User user = authService.getCurrentUser();
        if(sub.getAsUserFavSub().contains(user)){
            sub.removeUser(user);
        }else{
            sub.addUser(user);
        }
        subRepo.save(sub);
    }


    @Transactional
    public void markPostAsMyFav(Long postId){
        User currentUser = authService.getCurrentUser();
        Post post = this.getPostById(postId);
        if(post.getUsers().contains(currentUser)){
            post.removeUser(currentUser);
        }else{
            post.addUser(currentUser);
        }
        postRepo.save(post);
    }

    public boolean getMarkedPost(Long postId){
        Post post = this.getPostById(postId);
        return post.getUsers().contains(authService.getCurrentUser());
    }

    public Post getPostById(Long postId){
        return postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post Not Found"));
    }
}

package com.pttbackend.pttclone.service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.mapper.FavoriteListMapper;
import com.pttbackend.pttclone.mapper.PostMapper;
import com.pttbackend.pttclone.mapper.SubMapper;
import com.pttbackend.pttclone.model.FavoritePost;
import com.pttbackend.pttclone.model.FavoriteSub;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.repository.FavoritePostRepository;
import com.pttbackend.pttclone.repository.FavoriteSubRepository;
import com.pttbackend.pttclone.repository.PostRepository;
import com.pttbackend.pttclone.repository.SubRepository;
import com.pttbackend.pttclone.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class MyFavoriteListService {

    private final FavoriteSubRepository favoriteSubRepo;
    private final FavoritePostRepository favoritePostRepo;
    private final UserRepository userRepo;
    private final SubRepository subRepo;
    private final PostRepository postRepo;
    private final SubMapper subMapper;
    private final PostMapper postMapper;
    private final FavoriteListMapper favoriteListMapper;
    private final AuthenticationService authService;

    /**
     * 
     * @param user {@link User}
     * @return {@code List<String>}
     */
    @Transactional(readOnly = true)
    public List<PostResponse> getMyFavoritePosts(User user){
        log.info("List your Favorite Posts");
        List<FavoritePost> favPosts = favoritePostRepo.findByUser(user);
        
        return favPosts.stream()
                .map(favPost -> postRepo.findById(favPost.getFavPost().getId()).get()) 
                .map(postMapper::mapToPostResponse) 
                .collect(toList());
    }

    /**
     * 
     * @param user {@link User}
     * @return {@code List<String>}
     */
    @Transactional(readOnly = true)
    public List<SubDTO> getMyFavoriteSubs(User user){
        log.info("List your Favorite Subs");
        List<FavoriteSub> favSubs = favoriteSubRepo.findByUser(user);
        return favSubs.stream()
                .map(favSub -> subRepo.findById(favSub.getFavSub().getId()).get())
                .map(subMapper::mapToSubDTO)
                .collect(toList());
    } 

    /**
     * 
     * @param subname {@link Sub}'s name
     */
    public void saveSubAsMyFav(String subname){
        log.info("Saving this Sub As One of My Favorites");
        Sub sub = subRepo.findBySubname(subname).orElseThrow( () ->new RuntimeException("No Such Sub existing") );
        favoriteSubRepo.save(favoriteListMapper.mapToFavoriteSub(sub, authService.getCurrentUser()));
    }

    /**
     * 
     * @param postId {@link Post}'s id
     */
    public void savePostAsMyFav(Long postId){

        Post favPost = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Cant not find the Post"));
        User user =  authService.getCurrentUser();

        Optional<FavoritePost> post = favoritePostRepo.findByUserAndFavPost(user, favPost);

        // if post presents we delete it if not we add it
        post.ifPresentOrElse(
            p->  favoritePostRepo.deleteById(p.getFavoriteId()),
            ()-> favoritePostRepo.save(favoriteListMapper.mapToFavoritePost(favPost, user))
        );

    }

    public boolean getMarkedPost(Long postId){
        
        Post post = postRepo.findById(postId).orElseThrow(() -> new RuntimeException("Post Not Found"));
        
        Optional<FavoritePost> favPost = favoritePostRepo.findByUserAndFavPost(authService.getCurrentUser(), post);

        return favPost.isPresent();
    }
}

package com.pttbackend.pttclone.controller;

import java.util.List;

import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.service.AuthenticationService;
import com.pttbackend.pttclone.service.MyFavoriteListService;

import io.swagger.v3.oas.annotations.Operation;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.CREATED;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Mark the posts and subs in your favorite list
 * @see <a href="https://stackoverflow.com/questions/35155916/handling-ambiguous-handler-methods-mapped-in-rest-application-with-spring">
 *      [Error] Ambiguous handler methods </a>
 */
@RestController
@AllArgsConstructor
@RequestMapping("api/bookmark")
@Slf4j
public class BookmarkController {

    private final AuthenticationService authService;
    private final MyFavoriteListService myFavoriteListService;

    /**
     * Check My Favorite Post List
     * @return {@code List<FavoritePost>}
     */
    @Operation(summary = "GET ALL THE MARKED POSTS OF THE USER")
    @GetMapping("/getMyFavoritePosts")
    public ResponseEntity<List<PostResponse>> getMyFavoritePosts(){
        log.info("Get User Favorite Posts");
        return ResponseEntity.status(OK).body(myFavoriteListService.getMyFavoritePosts(authService.getCurrentUser()));    
    }
 
    /**
     * Check My Favorite Sub list
     * @return {@code List<FavoriteSub>}
     */
    @Operation(summary = "GET ALL THE MARKED SUBS OF THE USER")
    @GetMapping("/getMyFavoriteSubs")
    public ResponseEntity<List<SubDTO>> getMyFavoriteSubs(){
        log.info("Get User Favorite Subs");
        User user = authService.getCurrentUser();
        return ResponseEntity.status(OK).body(myFavoriteListService.getMyFavoriteSubs(user));    
    }
    
    /**
     * Add Sub to My Favorite List
     * @param subname {@code Sub#getSubname()}
     * @return {@code ResponseEntity<>(CREATED))}
     */
    @Operation(summary = "MARK THE SUB")
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/markThisSub/{subname}")
    public ResponseEntity<Void> addSubAsMyFavorite(@PathVariable String subname){
        myFavoriteListService.saveSubAsMyFav(subname);
        return new ResponseEntity<>(CREATED);
    }

    /**
     * Add Post to My Favorite List
     * @param postId {@code Post.getId()}
     * @return {@code ResponseEntity<Void>}
     */
    @Operation(summary = "MARK THE POST")
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/markThisPost/{postId}")
    public ResponseEntity<Void> addPostAsMyFavorite(@PathVariable Long postId){
        myFavoriteListService.savePostAsMyFav(postId);
        return new ResponseEntity<>(CREATED);
    }

    @Operation(summary = "GET MARKED POST OF THE USER")
    @GetMapping("/getMarkedPost/{postId}")
    public ResponseEntity<Void> getMarkedPost(@PathVariable Long postId){
        
        if(myFavoriteListService.getMarkedPost(postId)){
            return new ResponseEntity<>(CREATED);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

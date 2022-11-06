package com.pttbackend.pttclone.controller;

import java.util.List;
import java.util.Set;

import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.service.AuthenticationService;
import com.pttbackend.pttclone.service.BookmarkService;

// import io.swagger.v3.oas.annotations.Operation;

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
    private final BookmarkService bookmarkService;

    /**
     * @return {@code List<FavoritePost>}
     */
    //@Operation(summary = "GET ALL THE MARKED POSTS OF THE USER")
    @GetMapping("/getMyFavoritePosts")
    public ResponseEntity<List<PostResponse>> getMarkPosts(){
        log.info("Get User Favorite Posts");
        return ResponseEntity.status(OK).body(bookmarkService.getMyFavoritePosts(authService.getCurrentUser()));    
    }
 
    /**
     * @return {@code List<FavoriteSub>}
     */
    //@Operation(summary = "GET ALL THE MARKED SUBS OF THE USER")
    @GetMapping("/getMyFavoriteSubs")
    public ResponseEntity<Set<SubDTO>> getMarkSubs(){
        User user = authService.getCurrentUser();
        return ResponseEntity.status(OK).body(bookmarkService.getMyFavoriteSubs(user));    
    }
    
    /**
     * @param subname {@code Sub#getSubname()}
     * @return {@code ResponseEntity<>(CREATED))}
     */
    //@Operation(summary = "MARK THE SUB")
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/markThisSub/{subname}")
    public ResponseEntity<Void> markSubAsMyFavorite(@PathVariable String subname){
        bookmarkService.markSubAsMyFav(subname);
        return new ResponseEntity<>(CREATED);
    }
  
    /**
     * @param postId {@code Post.getId()}
     * @return {@code ResponseEntity<Void>}
     */
    //@Operation(summary = "MARK THE POST")
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/markThisPost/{postId}")
    public ResponseEntity<Void> markPostAsMyFavorite(@PathVariable Long postId){
        bookmarkService.markPostAsMyFav(postId);
        return new ResponseEntity<>(CREATED);
    }

    //@Operation(summary = "GET MARKED POST OF THE USER")
    @GetMapping("/getMarkedPost/{postId}")
    public ResponseEntity<Void> getMarkedPost(@PathVariable Long postId){
        
        if(bookmarkService.getMarkedPost(postId)){
            return new ResponseEntity<>(CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

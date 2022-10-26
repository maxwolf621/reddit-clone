package com.pttbackend.pttclone.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.pttbackend.pttclone.dto.PostRequest;
import com.pttbackend.pttclone.dto.PostResponse;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.Tag;
import com.pttbackend.pttclone.model.User;
import com.pttbackend.pttclone.model.Vote;
import com.pttbackend.pttclone.model.VoteType;

import static com.pttbackend.pttclone.model.VoteType.UPVOTE;
import static com.pttbackend.pttclone.model.VoteType.DOWNVOTE;
import com.pttbackend.pttclone.repository.CommentRepository;
import com.pttbackend.pttclone.repository.FavoritePostRepository;
import com.pttbackend.pttclone.repository.TagRepository;
import com.pttbackend.pttclone.repository.VoteRepository;
import com.pttbackend.pttclone.service.AuthenticationService;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;


/**
 * Map {@link PostRequest}, {@link Sub}, {@link User} to {@link Post} 
 * Map {@link Post} to {@link PostResponse} 
 */
@Mapper(componentModel = "spring" ,  implementationPackage = "com.pttbackend.pttclone.mapper")
public abstract class PostMapper {
    
    @Autowired
    private CommentRepository commentRepo;
    @Autowired
    private VoteRepository voteRepo;
    @Autowired
    private AuthenticationService authService;
    @Autowired
    private TagRepository tagRepo;
    @Autowired
    private FavoritePostRepository favoritePostRepo;


    /**
     * 
     * Map {@link PostRequest}, {@link Sub}, {@link User} to {@link Post} 
     * @param postRequest {@link PostRequest}
     * @param sub {@link Sub}
     * @param user {@link User}
     * @param tags {@link Tag}
     * @return {@link Post} 
     */
    @Mapping(target ="id" , ignore = true)
    @Mapping(target = "duration", expression = "java(java.time.Instant.now())")
    @Mapping(target ="description", expression = "java(postRequest.getDescription())")
    @Mapping(target = "voteCount", constant = "0")
    @Mapping(target = "sub" , source = "sub")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "tags", source = "tags")
    public abstract Post mapToPost(PostRequest postRequest, Set<Tag> tags, Sub sub, User user);


    /**
     * Map {@link Post} to {@link PostResponse}
     * @param post {@link Post}
     * @return {@link PostResponse}
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target= "subname" , source = "sub.subname")
    @Mapping(target ="username", source ="user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    @Mapping(target = "upVote", expression = "java(isPostUpVoted(post))") // for user
    @Mapping(target = "downVote", expression = "java(isPostDownVoted(post))") // for user
    @Mapping(target = "tagnames", expression="java(tagsToStringList(post.getTags()))")
    @Mapping(target = "marked" ,expression="java(postMarked(post))") // for user
    public abstract PostResponse mapToPostResponse(Post post);
    
    @Mapping(target="id" , ignore = true)
    @Mapping(target="tagname", source="tagname")
    public abstract Tag mapToTag(String tagname);
    
    Set<Tag> toTagList(Set<String> tagNames){    
        Set<Tag> tags = tagNames.stream().map(this::mapToTag).collect(toSet());
        tagRepo.saveAll(tags);
        return tags;
    }

    Set<String> tagsToStringList(Set<Tag> tags){
        return tags.stream().map(Tag::getTagname).collect(toSet());
    }

    Integer commentCount(Post post) {
        return commentRepo.findByPost(post).size();
    }

    // duration
    String getDuration(Post post) {
        return TimeAgo.using(post.getDuration().toEpochMilli());
    }

    boolean isPostUpVoted(Post post) {
        return checkVoteType(post, UPVOTE);
    }

    boolean isPostDownVoted(Post post) {
        return checkVoteType(post, DOWNVOTE);
    }

    boolean checkVoteType(Post post, VoteType voteType) {
        if (authService.isUserLoggedIn()) {
            Optional<Vote> voteForPostByUser = voteRepo.findTopByPostAndUserOrderByVoteIdDesc(post,authService.getCurrentUser());
            return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
        }
        return false;
    }

    boolean postMarked(Post post){
        if( authService.isUserLoggedIn()){
            return favoritePostRepo.findByUserAndFavPost(authService.getCurrentUser(), post).isPresent();
        }
        return false;
    }
}
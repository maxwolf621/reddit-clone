package com.pttbackend.pttclone.model;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.lang.Nullable;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.CascadeType.ALL;

/**
 * <p> Post's Entity </p>
 * <p> Do not using {@code @ToString} and {@code @Data} to avoid stackoverflow error </p>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post") 
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private long id;
    
    @NotBlank(message = "PostName Required")
    @Column(name = "post_name")
    private String postname;
    
    @Nullable
    @Lob
    @NotBlank(message = "Description Required")
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_date")
    private Instant duration;
    
    @Nullable
    @Column(name = "url")
    private String url;

    @Column(name = "vote_count")
    private Integer voteCount;

    /**
     * who create a post
     * add a column named userId reference table User's pk 
     * userId 
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  referencedColumnName = "user_id")
    private User user;
    
    /**
     * This Post is in which sub 
     * (bidirectional)
     */ 
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_id")
    private Sub sub; 

    /**
     * A post can be shared
     * with users who mark it 
     * as one of their favorite posts
     * this field is maintained by other table (favPost)
     */
    @OneToMany(fetch= LAZY,
               mappedBy = "favPost",
               cascade = ALL,
               orphanRemoval = true)
    private List<FavoritePost> favoritePosts;

    /**
     * A post can have many votes
     * e.g. down-vote  and up-vote ...
     * this field is maintained by other table (post)
     */
    @OneToMany(fetch = LAZY,
               cascade = ALL,
               mappedBy = "post",
               orphanRemoval = true)
    private List<Vote> votes;


    
    /**
     * Create Many To Many Third Join Table btw Post and Tag 
     */
    @ManyToMany(
        fetch = LAZY,
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH
        }
    )
    @JoinTable(
        name = "post_tag",
        joinColumns = @JoinColumn(name = "ref_post_id"),
        inverseJoinColumns = @JoinColumn(name = "ref_tag_id")
    )
    private Set<Tag> tags;

}
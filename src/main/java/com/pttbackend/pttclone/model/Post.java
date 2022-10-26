package com.pttbackend.pttclone.model;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.springframework.lang.Nullable;

import static javax.persistence.FetchType.LAZY;

/**
 * <p> Post's Entity </p>
 * <p> Do not using {@code @ToString} and {@code @Data} to avoid overflow error </p>
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "post") 
public class Post {

    // columns 
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  referencedColumnName = "user_id")
    private User user;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_id")
    private Sub sub; 

    // ---------------------------

    @OneToMany(fetch= LAZY,
               mappedBy = "favPost",
               cascade = {
                CascadeType.MERGE, 
                CascadeType.REFRESH, 
                CascadeType.PERSIST 
            },
               orphanRemoval = true)
    private List<FavoritePost> favoritePosts;

    @OneToMany(fetch = LAZY,
               cascade = {
                CascadeType.MERGE, 
                CascadeType.REFRESH, 
                CascadeType.PERSIST
            },
               mappedBy = "post",
               orphanRemoval = true)
    private List<Vote> votes;

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

    @OneToMany(fetch = LAZY,
        cascade = {
            CascadeType.MERGE, 
            CascadeType.REFRESH, 
            CascadeType.PERSIST
        },
        mappedBy = "post",
        orphanRemoval = true
    )
    private Set<Comment> comments;
}
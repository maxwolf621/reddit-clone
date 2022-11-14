package com.pttbackend.pttclone.model;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import java.time.Instant;
import java.util.Set;

import org.springframework.lang.Nullable;

import static javax.persistence.FetchType.LAZY;

/**
 * <p> Post's Entity </p>
 * <p> Do not using {@code @ToString} and {@code @Data} to avoid overflow error </p>
 */
@NamedEntityGraphs(
    @NamedEntityGraph(
        name = "post_sub_user_tags",
        attributeNodes = {
            //@NamedAttributeNode("id"),
            //@NamedAttributeNode("duration"),
            //@NamedAttributeNode("postname"),
            //@NamedAttributeNode("description"),
            //@NamedAttributeNode("url"),
            @NamedAttributeNode("user"),
            @NamedAttributeNode("tags"),
            @NamedAttributeNode("comments"),
            @NamedAttributeNode("sub"),
            @NamedAttributeNode("users")
        }
    )
)
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "post") 
@ToString
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

    /** ----   associations ----  */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",  referencedColumnName = "user_id")
    private User user;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_id")
    private Sub sub; 

    /*    
    @OneToMany(fetch = LAZY,
               cascade = {
                   CascadeType.MERGE, 
                   CascadeType.REFRESH, 
                   CascadeType.PERSIST 
               },
               mappedBy = "post",
               orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE,
     org.hibernate.annotations.CascadeType.DELETE,
     org.hibernate.annotations.CascadeType.MERGE,
     org.hibernate.annotations.CascadeType.PERSIST})    
    private Set<Vote> votes;
    */

    // bookmark
    @ManyToMany(
        fetch = LAZY,
        cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.DETACH
        }
    )
    @JoinTable(     
        name = "post_user",
        joinColumns = @JoinColumn(name = "post_id", updatable = true),
        inverseJoinColumns = @JoinColumn(name = "user_id", updatable = true)
    )
    private Set<User> users;
    public void addUser(User user){
        users.add(user);
        user.getFavPosts().add(this);
    }
    public void removeUser(User user){
        users.remove(user);
        user.getFavPosts().remove(this);
    }

    // tags
    @ManyToMany(
        fetch = LAZY,
        cascade = {
            CascadeType.ALL,
        }
    )
    @JoinTable(
        name = "post_tag",
        joinColumns = @JoinColumn(name = "ref_post_id"),
        inverseJoinColumns = @JoinColumn(name = "ref_tag_id")
    )
    private Set<Tag> tags;
    public boolean addTags(Tag tag){
        return this.tags.add(tag) && tag.getPosts().add(this);
    }
    public boolean removeTags(Tag tag){
        return this.tags.remove(tag) && tag.getPosts().remove(this);
    }

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
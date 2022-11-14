package com.pttbackend.pttclone.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.validation.constraints.NotBlank;

import static javax.persistence.GenerationType.SEQUENCE;


@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@EqualsAndHashCode(exclude = "posts")
@Entity @Table(name = "sub") 
@NamedEntityGraph(
    name = "sub-posts",
    attributeNodes = {
        @NamedAttributeNode("id"),
        @NamedAttributeNode("subname"),
        @NamedAttributeNode("description"),
        @NamedAttributeNode("createdDate"),
        @NamedAttributeNode("user"),
        @NamedAttributeNode("posts")
    }
)
public class Sub {
    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @Column(name = "sub_id")
    private Long id;
    
    @NotBlank(message = "Community name is required")
    @Column(name = "name")
    private String subname;

    @NotBlank(message = "Description is required")
    @Column(name = "description")
    private String description;
    
    @Column(name = "created_date")
    private Instant createdDate;

    // ---- associations ---

    // creator
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // sub's posts
    @OneToMany(fetch = FetchType.LAZY, 
               mappedBy ="sub",
               cascade = {
                CascadeType.MERGE, 
                CascadeType.REFRESH, 
                CascadeType.PERSIST 
            },
               orphanRemoval = true)
    private List<Post> posts;

    // as some users' favorite sub
    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = {
            CascadeType.MERGE, 
            CascadeType.REFRESH, 
            CascadeType.PERSIST 
        }
    )
    @JoinTable(
        name = "sub_user",
        joinColumns = @JoinColumn(name="sub_id"),
        inverseJoinColumns = @JoinColumn(name="user_id")
    )
    private Set<User> asUserFavSub = new HashSet<>();
    public void addUser(User user){
        asUserFavSub.add(user);
        user.getFavSubs().add(this);
    }
    public boolean removeUser(User user){
        boolean a = asUserFavSub.remove(user);
        boolean b = user.getFavSubs().remove(this);
        return a && b;
    }
}

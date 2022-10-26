package com.pttbackend.pttclone.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.AUTO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "favorite_post")
public class FavoritePost {

    @Id
    @Column(name = "favorite_id")
    @GeneratedValue(strategy = AUTO)
    private Long favoriteId;

    /**
     * A user can have many favorite posts
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * A post can be added 
     * as different 
     * user's favorite post list
     */
    @ManyToOne(fetch = LAZY)    
    @JoinColumn(name = "post_id")
    private Post favPost;

}

package com.pttbackend.pttclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.Instant;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="comment_id" , unique = true)
    private Long id;
    
    @NotEmpty
    @Column(name = "comment_text")
    private String text;
    
    @Column(name = "create_date")
    private Instant duration;

    /**
     * Comment In this Post
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    
    /**
     * User who comments    
     */ 
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * comment (replies to) to Comment 
     * {@code optional = true} where rootComment has no 
     * comment to reply
     */
    @ManyToOne(optional = true , fetch = LAZY)
    @JoinColumn(name= "repliedComment_id")
    private Comment rootComment;
    
    /**
     * A comment can hae many replied comments
     */
    @OneToMany(
        fetch = LAZY,
        mappedBy = "rootComment"
    )
    private List<Comment> childComments;
}

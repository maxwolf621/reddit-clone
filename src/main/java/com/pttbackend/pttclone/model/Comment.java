package com.pttbackend.pttclone.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiModelProperty;

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
    
    // columns 

    @ApiModelProperty(value = "comment_id")
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name="comment_id" , unique = true)
    private Long id;
    
    @ApiModelProperty(value = "comment_text")
    @NotEmpty
    @Column(name = "comment_text")
    private String text;
    
    @Column(name = "create_date")
    private Instant duration;

    // fks ---

    @ApiModelProperty(value = "post_id")
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = true , fetch = LAZY)
    @JoinColumn(name= "repliedComment_id")
    private Comment rootComment;
    
    // -----
    
    @OneToMany(
        fetch = LAZY,
        mappedBy = "rootComment"
    )
    private List<Comment> childComments;
}

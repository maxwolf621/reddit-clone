package com.pttbackend.pttclone.model;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

import static javax.persistence.GenerationType.SEQUENCE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "sub") 
public class Sub {
    @Id
    @GeneratedValue(strategy = SEQUENCE)
    @Column(name = "sub_id")
    private Long Id;
    
    @NotBlank(message = "Community name is required")
    @Column(name = "name")
    private String subname;

    @NotBlank(message = "Description is required")
    @Column(name = "description")
    private String description;
    
    @Column(name = "createdDate")
    private Instant createdDate;

    @OneToMany(fetch = FetchType.LAZY, 
               mappedBy ="sub",
               cascade = {
                CascadeType.MERGE, 
                CascadeType.REFRESH, 
                CascadeType.PERSIST 
            },
               orphanRemoval = true)
    private List<Post> posts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(fetch = FetchType.LAZY,
               mappedBy = "favSub",
               cascade = {
                CascadeType.MERGE, 
                CascadeType.REFRESH, 
                CascadeType.PERSIST 
            },
               orphanRemoval = true)
    private List<FavoriteSub> favoriteSubs; 
    
}

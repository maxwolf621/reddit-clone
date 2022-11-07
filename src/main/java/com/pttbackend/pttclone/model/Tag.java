package com.pttbackend.pttclone.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static javax.persistence.FetchType.LAZY;

@EqualsAndHashCode
@Setter @Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="tag")
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude // Reuse the tag for new post
    private Long id;

    @Column(name = "tag_name", unique = true)
    private String tagname;
    
    //@EqualsAndHashCode.Exclude // allow multiple posts use this tag
    @ManyToMany(
        fetch = LAZY,
        mappedBy = "tags"
    )
    private Set<Post> posts;

}


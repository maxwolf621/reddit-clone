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
@Table(name = "favorite_sub") 
public class FavoriteSub {

    @Id
    @GeneratedValue(strategy = AUTO )
    @Column(name = "favorite_id")
    private Long favoriteId;

    /**
     * User can store multiple subs
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Sub can be added in different user favorite sub list
     */
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "sub_id")
    private Sub favSub;
}

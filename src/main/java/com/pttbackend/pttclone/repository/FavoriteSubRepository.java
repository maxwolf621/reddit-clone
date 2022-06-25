package com.pttbackend.pttclone.repository;

import com.pttbackend.pttclone.model.FavoriteSub;
import com.pttbackend.pttclone.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteSubRepository extends JpaRepository<FavoriteSub, Long> {

    List<FavoriteSub> findByUser(User user);
}

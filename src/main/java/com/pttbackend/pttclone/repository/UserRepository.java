package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.pttbackend.pttclone.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String string);
    Optional<User> findByMail(String mail);
   
    @Modifying
    @Query("select u from User u where u.password = ?1")
    Optional<User> findByPassword(String password);

    int deleteByMail(String mail);

    @Modifying
    @Query("delete from User u where u.legit = false")
    void deleteIllegitimateUser();
}

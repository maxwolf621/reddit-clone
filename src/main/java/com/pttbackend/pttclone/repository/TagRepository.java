package com.pttbackend.pttclone.repository;

import com.pttbackend.pttclone.model.Tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {
 
    Optional<Tag> findByTagname(String tagname);
}

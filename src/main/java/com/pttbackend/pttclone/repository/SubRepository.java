package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import com.pttbackend.pttclone.model.Sub;

@Repository
public interface SubRepository extends JpaRepository<Sub, Long> {
    Optional<Sub> findBySubname(String subname);
}

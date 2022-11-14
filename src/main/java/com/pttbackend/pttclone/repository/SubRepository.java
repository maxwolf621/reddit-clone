package com.pttbackend.pttclone.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.Sub;

@Repository
public interface SubRepository extends JpaRepository<Sub, Long> {

    @Query("Select new com.pttbackend.pttclone.dto.SubDTO(s.id,s.subname, s.description) From Sub s")
    List<SubDTO> findAllSubs();

    //@EntityGraph(value = "sub-posts", type= EntityGraph.EntityGraphType.LOAD)
    @Query("Select new com.pttbackend.pttclone.dto.SubDTO(s.id,s.subname, s.description) From Sub s WHERE s.subname=?1") 
    Optional<SubDTO> findSubBySubname(String subname);

    Optional<Sub> findBySubname(String subname);

    @Query("Select new com.pttbackend.pttclone.dto.SubDTO(s.id,s.subname, s.description) From Sub s WHERE s.id=?1") 
    Optional<SubDTO> findBySubId(Long id);


}

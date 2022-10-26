package com.pttbackend.pttclone.service;

import java.util.Set;
import java.util.stream.Collectors;

import com.pttbackend.pttclone.model.Tag;
import com.pttbackend.pttclone.repository.TagRepository;


import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TagService {
    private final TagRepository tagRepo;

    public Set<String> getAlltags(){
        log.info("get all tags");
        return tagRepo.findAll().stream().map(Tag::getTagname).collect(Collectors.toSet());
    }

}

package com.pttbackend.pttclone.mapper;

import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SubMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "subname", source = "subname")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "postsCount" , expression = "java(getSize(sub))")
    SubDTO mapToSubDTO(Sub sub);

    default Integer getSize(Sub sub){
        return sub.getPosts().size();
    }

    @Mapping(target = "subname", expression = "java(subDTO.getSubname())")
    @Mapping(target = "description", expression = "java(subDTO.getDescription())")
    @Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "asUserFavSub" , ignore = true)
    Sub mapToSub(SubDTO subDTO, User user);
}

  
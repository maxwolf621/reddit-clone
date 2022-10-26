package com.pttbackend.pttclone.mapper;

import com.pttbackend.pttclone.dto.SubDTO;
import com.pttbackend.pttclone.model.Sub;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",  implementationPackage = "com.pttbackend.pttclone.mapper")
public interface SubMapper {

    @Mapping(target = "postsCount" , expression = "java(getSize(sub))")
    SubDTO mapToSubDTO(Sub sub);

    default Integer getSize(Sub sub){
        return sub.getPosts().size();
    }
}

  
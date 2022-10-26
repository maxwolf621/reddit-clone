package com.pttbackend.pttclone.mapper;

import com.pttbackend.pttclone.model.FavoritePost;
import com.pttbackend.pttclone.model.FavoriteSub;
import com.pttbackend.pttclone.model.Post;
import com.pttbackend.pttclone.model.Sub;
import com.pttbackend.pttclone.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", implementationPackage = "com.pttbackend.pttclone.mapper" )
public interface FavoriteListMapper {
    
    @Mapping(target = "favoriteId", ignore = true)
    @Mapping(target = "favSub" , source = "sub")
    @Mapping(target = "user", source = "user")
    FavoriteSub mapToFavoriteSub(Sub sub, User user);


    @Mapping(target = "favoriteId", ignore = true)
    @Mapping(target = "favPost" , source = "post")
    @Mapping(target = "user", source = "user")
    FavoritePost mapToFavoritePost(Post post, User user);

}

package com.pttbackend.pttclone.mapper;

import com.pttbackend.pttclone.dto.UserDTO;
import com.pttbackend.pttclone.model.User;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * @see <a href="https://www.baeldung.com/spring-data-partial-update">
 *      reference </a>
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO mapToUserDTO(User user);

    /** 
     * update only the modified values via {@code @BeanMapping}
     * @param userDTO {@link UserDTO}
     * @param user {@link User}
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDTO userDTO, @MappingTarget User user);
}

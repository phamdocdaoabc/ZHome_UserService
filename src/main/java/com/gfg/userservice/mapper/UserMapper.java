package com.gfg.userservice.mapper;

import com.gfg.userservice.domain.dto.user.UserDTO;
import com.gfg.userservice.domain.dto.user.UserDetailDTO;
import com.gfg.userservice.domain.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    UserEntity toEnity(UserDTO userDTO);

    UserDTO toDTO(UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(UserDetailDTO userDetailDTO, @MappingTarget UserEntity userEntity);
}

package com.gfg.userservice.mapper;

import com.gfg.userservice.domain.dto.postpackage.PostPackageDTO;
import com.gfg.userservice.domain.entity.PostPackageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostPackageMapper {
    @Mapping(target = "id", ignore = true)
    PostPackageEntity toEnity(PostPackageDTO postPackageDTO);

    PostPackageDTO toDTO(PostPackageEntity postPackageEntity);

    @Mapping(target = "id", ignore = true)
    void updateEntity(PostPackageDTO dto, @MappingTarget PostPackageEntity entity);

    List<PostPackageDTO> toDTOList(List<PostPackageEntity> postPackageEntities);
}

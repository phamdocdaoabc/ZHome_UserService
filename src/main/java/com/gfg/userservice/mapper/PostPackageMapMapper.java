package com.gfg.userservice.mapper;

import com.gfg.userservice.domain.dto.postpackage.PostPackageMapDTO;
import com.gfg.userservice.domain.dto.postpackage.PostPackageMapPage;
import com.gfg.userservice.domain.entity.PostPackageEntity;
import com.gfg.userservice.domain.entity.PostPackageMapEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PostPackageMapMapper {
    @Mapping(target = "id", ignore = true)
    PostPackageMapEntity toEnity(PostPackageMapDTO dto);

    PostPackageMapDTO toDTO(PostPackageMapEntity entity);

    PostPackageMapPage toPage(PostPackageMapEntity entity);

}

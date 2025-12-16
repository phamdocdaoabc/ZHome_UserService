package com.gfg.userservice.mapper;

import com.gfg.userservice.domain.dto.user.CredentialDTO;
import com.gfg.userservice.domain.entity.CredentialEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CredentialMapper {
    CredentialEntity toEntity(CredentialDTO credentialDTO);

    CredentialDTO toDTO(CredentialEntity credentialEntity);

}


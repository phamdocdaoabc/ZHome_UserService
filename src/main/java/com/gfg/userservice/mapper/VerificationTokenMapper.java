package com.gfg.userservice.mapper;

import com.gfg.userservice.domain.entity.VerificationTokenEntity;
import com.gfg.userservice.domain.dto.user.VerificationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface VerificationTokenMapper {
    VerificationDTO toDTO(VerificationTokenEntity verificationTokenEntity);

    VerificationTokenEntity toEnity(VerificationDTO verificationTokenDto);
}

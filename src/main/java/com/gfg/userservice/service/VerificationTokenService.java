package com.gfg.userservice.service;

import com.gfg.userservice.domain.dto.user.VerificationDTO;

import java.util.List;

public interface VerificationTokenService {

    List<VerificationDTO> findAll();
    VerificationDTO findById(Long verificationTokenId);
    VerificationDTO save(VerificationDTO verificationTokenDto);
    VerificationDTO update(VerificationDTO verificationTokenDto);
    VerificationDTO update(Long verificationTokenId, VerificationDTO verificationTokenDto);
    void deleteById(Long verificationTokenId);

}

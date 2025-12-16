package com.gfg.userservice.service;

import com.gfg.userservice.domain.dto.user.CredentialDTO;
import com.gfg.userservice.domain.dto.user.CredentialStatusDTO;

import java.util.List;

public interface CredentialService {
    List<CredentialDTO> findAll();
    CredentialDTO findById(Long credentialId);
    CredentialDTO save(CredentialDTO credentialDto);
    CredentialDTO update(CredentialDTO credentialDto);
    void deleteById(Long credentialId);
    void updateStatusLocked(CredentialStatusDTO credentialStatusDTO);
}

package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.user.CredentialDTO;
import com.gfg.userservice.domain.dto.user.CredentialStatusDTO;
import com.gfg.userservice.domain.entity.CredentialEntity;
import com.gfg.userservice.domain.entity.UserEntity;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.mapper.CredentialMapper;
import com.gfg.userservice.repository.CredentialRepository;
import com.gfg.userservice.repository.UserRepository;
import com.gfg.userservice.service.CredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CredentialServiceImpl implements CredentialService {
    private final CredentialRepository credentialRepository;
    private final UserRepository userRepository;
    private final CredentialMapper credentialMapper;

    @Override
    public List<CredentialDTO> findAll() {
        log.info("CredentialsDTO, find all the credentials");
        return this.credentialRepository.findAll()
                .stream()
                .map(credentialMapper::toDTO)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public CredentialDTO findById(Long credentialId) {
        log.info("Credentials, Find the Credentials by Id");
        return this.credentialRepository
                .findById(credentialId)
                .map(credentialMapper::toDTO)
                .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));
    }

    @Override
    @Transactional
    public CredentialDTO save(CredentialDTO credentialDto) {
        log.info("Saving the Credentials");

        // Map CredentialDTO to Credential entity
        CredentialEntity credentialEntity = credentialMapper.toEntity(credentialDto);

        // Save the credential
        CredentialEntity savedCredentialEntity = credentialRepository.save(credentialEntity);

        // Map the saved Credential entity back to CredentialDTO and return
        return credentialMapper.toDTO(savedCredentialEntity);
    }


    @Override
    public CredentialDTO update(CredentialDTO credentialDto) {
        log.info("CredentialDTO, Update the Credentials");
        return credentialMapper.toDTO(this.credentialRepository.save(credentialMapper.toEntity(credentialDto)));
    }

    @Override
    public void deleteById(Long credentialId) {
        log.info("CredentialDTO, Delete the Credentials");
        this.credentialRepository.deleteById(credentialId);

    }

    @Override
    @Transactional
    public void updateStatusLocked(CredentialStatusDTO credentialStatusDTO) {
        CredentialEntity credentialEntity = credentialRepository.findByUserId(credentialStatusDTO.getUserId())
                .orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));
        credentialEntity.setIsAccountNonLocked(credentialStatusDTO.getIsAccountNonLocked());
        credentialRepository.save(credentialEntity);
    }
}

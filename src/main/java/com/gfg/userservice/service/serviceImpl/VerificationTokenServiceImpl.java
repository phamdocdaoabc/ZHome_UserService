package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.user.VerificationDTO;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.mapper.VerificationTokenMapper;
import com.gfg.userservice.repository.VerificationTokenRepository;
import com.gfg.userservice.service.VerificationTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final VerificationTokenMapper verificationTokenMapper;

    @Override
    public List<VerificationDTO> findAll() {
        log.info("VerificationDTo, Fetch all the verification Tokens");
        return this.verificationTokenRepository
                .findAll().stream().map(verificationTokenMapper::toDTO)
                .distinct().collect(Collectors.toList());
    }

    @Override
    public VerificationDTO findById(Long verificationTokenId) {
        log.info("VerificationDTo, Fetch all the verification Tokens using VerificationId");
        return this.verificationTokenRepository.findById(verificationTokenId)
                .map(verificationTokenMapper::toDTO)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_007));
    }

    @Override
    public VerificationDTO save(VerificationDTO verificationTokenDto) {
        log.info("VerificationDTo, Save the verification Tokens");
        return verificationTokenMapper.toDTO(this.verificationTokenRepository.save(verificationTokenMapper.toEnity(verificationTokenDto)));
    }

    @Override
    public VerificationDTO update(VerificationDTO verificationTokenDto) {
        log.info("VerificationDTo, Update the verification Tokens");
        return verificationTokenMapper.toDTO(this.verificationTokenRepository.save(verificationTokenMapper.toEnity(verificationTokenDto)));
    }

    @Override
    public VerificationDTO update(Long verificationTokenId, VerificationDTO verificationTokenDto) {
        log.info("VerificationDTo, Update the verification Tokens by Using VerificationTokenId");
        return verificationTokenMapper.toDTO(this.verificationTokenRepository.save(verificationTokenMapper.toEnity(this.findById(verificationTokenId))));
    }

    @Override
    public void deleteById(Long verificationTokenId) {
        log.info("VerificationDTo, Update the verification Tokens");
        this.verificationTokenRepository.deleteById(verificationTokenId);

    }
}

package com.gfg.userservice.service.serviceImpl;

import com.gfg.userservice.constant.ErrorCodes;
import com.gfg.userservice.domain.dto.user.CredentialDTO;
import com.gfg.userservice.domain.dto.user.UserDTO;
import com.gfg.userservice.domain.dto.user.UserDetailDTO;
import com.gfg.userservice.domain.dto.user.UserFilter;
import com.gfg.userservice.domain.entity.CredentialEntity;
import com.gfg.userservice.domain.entity.UserEntity;
import com.gfg.userservice.exceptions.AppException;
import com.gfg.userservice.mapper.CredentialMapper;
import com.gfg.userservice.mapper.UserMapper;
import com.gfg.userservice.repository.CredentialRepository;
import com.gfg.userservice.repository.UserRepository;
import com.gfg.userservice.repository.specs.CredentialSpecification;
import com.gfg.userservice.repository.specs.SpecificationUtils;
import com.gfg.userservice.repository.specs.UserSpecification;
import com.gfg.userservice.service.EmailService;
import com.gfg.userservice.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CredentialRepository credentialRepository;
    private final CredentialMapper credentialMapper;


    @Override
    public Page<UserDTO> findAll(UserFilter userFilter, Pageable pageable) {
        // filter by credential fields
        if (Objects.nonNull(userFilter.getIsEnabled()) || Objects.nonNull(userFilter.getIsAccountNonLocked())
                || Objects.nonNull(userFilter.getRole())) {
            Specification<CredentialEntity> specCredential = Specification.where(null);
            specCredential = SpecificationUtils.addIfNotNull(specCredential, userFilter.getRole(), CredentialSpecification::hasRole);
            specCredential = SpecificationUtils.addIfNotNull(specCredential, userFilter.getIsEnabled(), CredentialSpecification::hasEnabled);
            specCredential = SpecificationUtils.addIfNotNull(specCredential, userFilter.getIsAccountNonLocked(), CredentialSpecification::hasAccountNonLocked);
            Page<CredentialEntity> credentialEntityPage = credentialRepository.findAll(specCredential, pageable);
            Set<Long> userIds = credentialEntityPage.stream().map(CredentialEntity::getUserId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            userFilter.setIds(userIds);
        }
        // filter by user fields
        Specification<UserEntity> spec = Specification
                .where(null);
        spec = SpecificationUtils.addIfNotEmpty(spec, userFilter.getIds(), UserSpecification::hasIds);
        spec = SpecificationUtils.addIfHasText(spec, userFilter.getFullName(), UserSpecification::hasFullName);
        spec = SpecificationUtils.addIfHasText(spec, userFilter.getEmail(), UserSpecification::hasEmail);
        spec = SpecificationUtils.addIfHasText(spec, userFilter.getPhone(), UserSpecification::hasPhone);
        spec = SpecificationUtils.addIfHasText(spec, userFilter.getAddress(), UserSpecification::hasAddress);
        spec = SpecificationUtils.addIfNotNull(spec, userFilter.getSex(), UserSpecification::hasSex);
        Page<UserEntity> userEntityPage = userRepository.findAll(spec, pageable);
        Set<Long> userIds = userEntityPage.stream().map(UserEntity::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<CredentialEntity> credentialEntityList = credentialRepository.findAllByUserIdIn(userIds);
        Map<Long, CredentialDTO> credentialDTOMap = credentialEntityList.stream()
                .collect(Collectors.toMap(
                        CredentialEntity::getUserId,
                        credentialMapper::toDTO,
                        (existing, replacement) -> existing
                ));
        return userEntityPage.map(userEntity -> {
            UserDTO userDTO = userMapper.toDTO(userEntity);
            if (credentialDTOMap.containsKey(userDTO.getId()))
            {
                userDTO.setCredentialDTO(credentialDTOMap.get(userDTO.getId()));
            } else {
                throw new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND);
            }
            return userDTO;
        });
    }

    @Override
    public UserDTO findById(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND_ID, userId));
        CredentialEntity credentialEntity = credentialRepository.findByUserId(userId).orElseThrow(() -> new AppException(ErrorCodes.CREDENTIAL_NOT_FOUND));
        UserDTO userDTO = userMapper.toDTO(userEntity);
        userDTO.setCredentialDTO(credentialMapper.toDTO(credentialEntity));
        return userDTO;
    }

    @Override
    public Long update(UserDetailDTO userDetailDTO) {
        UserEntity userEntity = userRepository.findById(userDetailDTO.getId()).orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_FOUND_ID, userDetailDTO.getId()));
        userMapper.updateEntity(userDetailDTO, userEntity);
        userRepository.save(userEntity);
        return userEntity.getId();
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }
}

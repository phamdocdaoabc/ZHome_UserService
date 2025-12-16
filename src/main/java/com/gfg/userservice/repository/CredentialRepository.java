package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.CredentialEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface CredentialRepository extends JpaRepository<CredentialEntity,Long>, JpaSpecificationExecutor<CredentialEntity> {
    Optional<CredentialEntity> findByUserName(String userName);

    Optional<CredentialEntity> findByUserId(Long userId);

    Boolean existsByUserName(String userName);

    List<CredentialEntity> findAllByUserIdIn(Set<Long> userIds);
}

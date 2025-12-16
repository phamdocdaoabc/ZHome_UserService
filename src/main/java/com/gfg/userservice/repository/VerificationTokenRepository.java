package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationTokenEntity,Long> {
    boolean existsByVerifToken(String verifToken);
}

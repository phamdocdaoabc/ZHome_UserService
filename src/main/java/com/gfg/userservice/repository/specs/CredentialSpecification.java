package com.gfg.userservice.repository.specs;

import com.gfg.userservice.domain.entity.CredentialEntity;
import com.gfg.userservice.domain.entity.UserEntity;
import com.gfg.userservice.domain.enums.RoleBase;
import com.gfg.userservice.domain.enums.Sex;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class CredentialSpecification {
    public static Specification<CredentialEntity> hasRole(RoleBase role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }

    public static Specification<CredentialEntity> hasEnabled(Boolean isEnabled) {
        return (root, query, cb) -> cb.equal(root.get("isEnabled"), isEnabled);
    }

    public static Specification<CredentialEntity> hasAccountNonLocked(Boolean isAccountNonLocked) {
        return (root, query, cb) -> cb.equal(root.get("isAccountNonLocked"), isAccountNonLocked);
    }
}

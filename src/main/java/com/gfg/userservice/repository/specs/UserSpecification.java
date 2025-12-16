package com.gfg.userservice.repository.specs;

import com.gfg.userservice.domain.entity.UserEntity;
import com.gfg.userservice.domain.enums.Sex;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;

public class UserSpecification {
    public static Specification<UserEntity> hasIds(Collection<Long> ids) {
        return (root, query, cb) -> root.get("id").in(ids);
    }

    public static Specification<UserEntity> hasFullName(String fullName) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    public static Specification<UserEntity> hasEmail(String email) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
    }

    public static Specification<UserEntity> hasPhone(String phone) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("phone")), "%" + phone.toLowerCase() + "%");
    }

    public static Specification<UserEntity> hasAddress(String address) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%");
    }

    public static Specification<UserEntity> hasSex(Sex sex) {
        return (root, query, cb) -> cb.equal(root.get("sex"), sex);
    }
}

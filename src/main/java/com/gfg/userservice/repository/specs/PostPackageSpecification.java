package com.gfg.userservice.repository.specs;

import com.gfg.userservice.domain.entity.PostPackageEntity;
import com.gfg.userservice.domain.enums.PackageType;
import org.springframework.data.jpa.domain.Specification;

public class PostPackageSpecification {
    public static Specification<PostPackageEntity> hasType(PackageType packageType) {
        return (root, query, cb) -> cb.equal(root.get("packageType"), packageType);
    }
}

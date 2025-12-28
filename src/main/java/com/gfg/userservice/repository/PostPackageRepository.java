package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.PostPackageEntity;
import com.gfg.userservice.domain.enums.PackageType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PostPackageRepository extends JpaRepository<PostPackageEntity,Long>, JpaSpecificationExecutor<PostPackageEntity> {
    List<PostPackageEntity> findAllByPackageType(PackageType packageType);
}

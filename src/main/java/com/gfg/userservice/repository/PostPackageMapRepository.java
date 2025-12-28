package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.PostPackageMapEntity;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface PostPackageMapRepository extends JpaRepository<PostPackageMapEntity,Long>, JpaSpecificationExecutor<PostPackageMapEntity> {

    PostPackageMapEntity findByUserIdAndStatus(Long userId, PostPackageStatus status);
}

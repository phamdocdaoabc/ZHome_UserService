package com.gfg.userservice.repository;

import com.gfg.userservice.domain.entity.PostPackageMapEntity;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface PostPackageMapRepository extends JpaRepository<PostPackageMapEntity,Long>, JpaSpecificationExecutor<PostPackageMapEntity> {

    PostPackageMapEntity findByUserIdAndStatus(Long userId, PostPackageStatus status);

    List<PostPackageMapEntity> findAllByEndDateBeforeAndStatus(LocalDateTime endDateBefore, PostPackageStatus status);

    @Query("SELECT COUNT(ppm) FROM PostPackageMapEntity ppm " +
            "WHERE ppm.userId = :userId " +
            "AND ppm.startDate >= :startOfMonth " +
            "AND ppm.startDate <= :endOfMonth " +
            "AND ppm.packageId IN (SELECT pp.id FROM PostPackageEntity pp WHERE pp.packageType = 'BASIC')")
    long countBasicPackageInMonth(@Param("userId") Long userId,
                                  @Param("startOfMonth") LocalDateTime startOfMonth,
                                  @Param("endOfMonth") LocalDateTime endOfMonth);

}

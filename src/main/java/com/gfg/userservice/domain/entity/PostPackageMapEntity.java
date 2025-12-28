package com.gfg.userservice.domain.entity;

import com.gfg.userservice.audit.BaseEntity;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post_package_map")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostPackageMapEntity extends BaseEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;
    private Long userId;
    private Long packageId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private PostPackageStatus status;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "total_posts_allowed")
    private Long totalPostsAllowed;

    @Column(name = "current_post_count")
    private Long currentPostCount;
}

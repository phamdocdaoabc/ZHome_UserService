package com.gfg.userservice.domain.entity;

import com.gfg.userservice.audit.BaseEntity;
import com.gfg.userservice.domain.enums.PackageType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "post_packages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PostPackageEntity extends BaseEntity  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "package_type", length = 50, nullable = false)
    private PackageType packageType;

    @Column(name = "display_name", length = 250, nullable = false)
    private String displayName;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "duration_days", nullable = false)
    private Long durationDays;

    @Column(name = "listing_limit")
    private Long listingLimit;

    @Column(name = "post_duration")
    private Long postDuration;

    @Column(name = "priority")
    private Long priority;

    @Column(name = "auto_approve", nullable = false)
    private Boolean autoApprove;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "max_posts")
    private Long maxPosts;
}

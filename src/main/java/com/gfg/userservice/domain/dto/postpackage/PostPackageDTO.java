package com.gfg.userservice.domain.dto.postpackage;

import com.gfg.userservice.domain.enums.PackageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostPackageDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private PackageType packageType;
    private String displayName;
    private BigDecimal price;
    private Long durationDays;
    private Long listingLimit;
    private Long postDuration;
    private Long priority;
    private Boolean autoApprove;
    private String description;
    private Long maxPosts;
    private Boolean isRegisterable; // Cho phép đăng ký không?
    private Boolean isCurrentActive; // Gói hiện tại
}

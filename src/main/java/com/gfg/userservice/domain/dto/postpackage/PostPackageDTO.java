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
    private Integer durationDays;
    private Long listingLimit;
    private Integer postDuration;
    private Integer priority;
    private Boolean autoApprove;
    private String description;
    private Integer maxPosts;
}

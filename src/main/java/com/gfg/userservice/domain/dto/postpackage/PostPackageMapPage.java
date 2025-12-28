package com.gfg.userservice.domain.dto.postpackage;

import com.gfg.userservice.domain.dto.user.UserDTO;
import com.gfg.userservice.domain.enums.PackageType;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostPackageMapPage implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    private String fullName;
    private String imageUrl;
    private String email;
    private Long packageId;
    private PackageType packageType;
    private PostPackageStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalPostsAllowed;
    private Integer currentPostCount;
}

package com.gfg.userservice.domain.dto.postpackage;

import com.gfg.userservice.domain.dto.user.UserDTO;
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
public class PostPackageMapDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private UserDTO user;
    private PostPackageDTO postPackage;
    private PostPackageStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer totalPostsAllowed;
    private Integer currentPostCount;
}

package com.gfg.userservice.domain.dto.postpackage;

import com.gfg.userservice.domain.enums.PackageType;
import com.gfg.userservice.domain.enums.PostPackageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostPackageFilterDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private PackageType packageType;
    private PostPackageStatus status;
    private LocalDate startDate;
    private Set<Long> packageIds;
}

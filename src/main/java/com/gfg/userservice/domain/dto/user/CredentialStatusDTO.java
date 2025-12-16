package com.gfg.userservice.domain.dto.user;

import com.gfg.userservice.domain.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CredentialStatusDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long userId;
    private Boolean isAccountNonLocked;
}

package com.gfg.userservice.domain.dto.user;

import com.gfg.userservice.domain.enums.RoleBase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CredentialDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long credentialId;
    private String username;
    private String password;
    private RoleBase roleBase;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;
}

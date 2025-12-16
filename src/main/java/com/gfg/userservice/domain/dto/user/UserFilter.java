package com.gfg.userservice.domain.dto.user;

import com.gfg.userservice.domain.enums.RoleBase;
import com.gfg.userservice.domain.enums.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserFilter implements Serializable {

    private static final long serialVersionUID = 1L;
    private Set<Long> ids;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private Sex sex;
    // Credential
    private RoleBase role;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;
}

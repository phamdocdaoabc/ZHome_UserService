package com.gfg.userservice.domain.entity;

import com.gfg.userservice.audit.BaseEntity;
import com.gfg.userservice.domain.enums.RoleBase;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "credentials")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public final class CredentialEntity extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;
    @Column(name = "user_name",unique = true)
    private String userName;
    private String password;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private RoleBase role;
    private Boolean isEnabled;
    private Boolean isAccountNonLocked;
}


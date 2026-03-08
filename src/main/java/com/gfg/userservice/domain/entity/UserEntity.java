package com.gfg.userservice.domain.entity;

import com.gfg.userservice.audit.BaseEntity;
import com.gfg.userservice.domain.enums.Sex;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;


import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;


@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false, updatable = false)
    private Long id;

    private String firstName;
    private String lastName;
    private String fullName;
    private String imageUrl;
    @Email(message = "*Input must be in Email format!**")
    private String email;

    private String phone;

    private String address;

    private String bio;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    private LocalDate birthday;

}

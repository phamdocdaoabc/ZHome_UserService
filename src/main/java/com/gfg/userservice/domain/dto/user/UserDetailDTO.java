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
public class UserDetailDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String email;
    private String phone;
    private String address;
    private String bio;
    private Sex sex;
    private LocalDate birthday;
}

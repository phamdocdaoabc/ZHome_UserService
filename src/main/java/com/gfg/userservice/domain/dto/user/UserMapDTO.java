package com.gfg.userservice.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserMapDTO implements Serializable {
    private Long id;
    private String fullName;
    private String imageUrl;
    private String address;
}

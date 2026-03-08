package com.gfg.userservice.domain.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePasswordDTO {
    @NotBlank
    String currentPassword;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters.")
    private String newPassword;

    @NotBlank
    @Size(min = 6, message = "Password must be at least 6 characters.")
    private String confirmPassword;
}

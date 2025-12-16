package com.gfg.userservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {
    /**
     * Lấy toàn bộ đối tượng JWT (nếu cần truy cập raw claims)
     */
    public static Optional<Jwt> getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            return Optional.of(((JwtAuthenticationToken) authentication).getToken());
        }
        return Optional.empty();
    }

    /**
     * Method thay thế cho extractUsername cũ
     * Lấy Username của user đang đăng nhập hiện tại
     */
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Optional.empty();
        }
        // Với oauth2 resource server, getName() mặc định trả về claim "sub" (subject)
        return Optional.ofNullable(authentication.getName());
    }

    /**
     * Method mới: Lấy UserId (Vì ta đã lưu userId vào claim ở Auth Service)
     * Rất hữu ích khi cần lưu xuống DB (created_by_id)
     */
    public static Optional<Long> getCurrentUserId() {
        return getCurrentJwt().map(jwt -> {
            // Lấy claim "userId" mà ta đã put vào lúc tạo token
            return jwt.getClaim("userId");
        });
    }

    /**
     * Kiểm tra User hiện tại có quyền gì không
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }
}
